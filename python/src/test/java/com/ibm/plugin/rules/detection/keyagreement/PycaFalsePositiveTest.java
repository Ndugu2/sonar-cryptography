package com.ibm.plugin.rules.detection.keyagreement;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.KeyAgreement;
import com.ibm.plugin.TestBase;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.python.api.PythonCheck;
import org.sonar.plugins.python.api.PythonVisitorContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.python.checks.utils.PythonCheckVerifier;

class PycaFalsePositiveTest extends TestBase {

    @Test
    void test() {
        // This will trigger the asserts method for each finding
        PythonCheckVerifier.verify(
                "src/test/files/rules/detection/keyagreement/PycaFalsePositiveTestFile.py", this);
    }

    @Override
    public void asserts(
            int findingId,
            @Nonnull DetectionStore<PythonCheck, Tree, Symbol, PythonVisitorContext> detectionStore,
            @Nonnull List<INode> nodes) {

        // We expect only ONE finding (findingId == 0) which is the legitimate x448.generate()
        // The generic model.generate() should NOT trigger any finding.
        
        assertThat(findingId).isEqualTo(0);
        
        INode keyAgreementNode = nodes.get(0);
        assertThat(keyAgreementNode.getKind()).isEqualTo(KeyAgreement.class);
        assertThat(keyAgreementNode.asString()).isEqualTo("x448");
    }
}
