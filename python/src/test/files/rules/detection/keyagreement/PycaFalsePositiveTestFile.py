class Model:
    def generate(self, x):
        return x

model = Model()
# This should NOT be detected as X448 or X25519 key generation
ids = model.generate(10)

def foo():
    # Another generic call
    x = some_other_object.generate()
    return x

from cryptography.hazmat.primitives.asymmetric import x448
# This SHOULD be detected
private_key = x448.X448PrivateKey.generate() # Noncompliant
