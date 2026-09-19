using System.Security.Cryptography;
using System.Text;

namespace KnowTheMice.Security;

public class CryptoEngine
{
    private readonly ECDiffieHellmanCng _ecdh;
    public byte[] PublicKeyBytes { get; }
    public string PublicKeyBase64 => Convert.ToBase64String(PublicKeyBytes);

    public CryptoEngine()
    {
        _ecdh = new ECDiffieHellmanCng(ECCurve.NamedCurves.nistP256);
        _ecdh.KeyDerivationFunction = ECDiffieHellmanKeyDerivationFunction.Hash;
        _ecdh.HashAlgorithm = CngAlgorithm.Sha256;
        PublicKeyBytes = _ecdh.PublicKey.ToByteArray();
    }

    public byte[] DeriveSharedSecret(byte[] otherPartyPublicKeyBytes)
    {
        using var otherKey = ECDiffieHellmanCngPublicKey.FromByteArray(otherPartyPublicKeyBytes, CngKeyBlobFormat.EccPublicBlob);
        return _ecdh.DeriveKeyMaterial(otherKey);
    }

    public static byte[] DeriveSessionKey(byte[] sharedSecret, string salt)
    {
        using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(salt));
        return hmac.ComputeHash(sharedSecret);
    }

    public static (byte[] Ciphertext, byte[] Nonce, byte[] Tag) EncryptAesGcm(byte[] plaintext, byte[] key)
    {
        using var aes = new AesGcm(key, AesGcm.TagByteSizes.MaxSize);
        byte[] nonce = new byte[AesGcm.NonceByteSizes.MaxSize];
        RandomNumberGenerator.Fill(nonce);

        byte[] ciphertext = new byte[plaintext.Length];
        byte[] tag = new byte[AesGcm.TagByteSizes.MaxSize];

        aes.Encrypt(nonce, plaintext, ciphertext, tag);
        return (ciphertext, nonce, tag);
    }

    public static byte[] DecryptAesGcm(byte[] ciphertext, byte[] nonce, byte[] tag, byte[] key)
    {
        using var aes = new AesGcm(key, AesGcm.TagByteSizes.MaxSize);
        byte[] plaintext = new byte[ciphertext.Length];
        aes.Decrypt(nonce, ciphertext, tag, plaintext);
        return plaintext;
    }

    public static string ComputeHmac(byte[] key, string data)
    {
        using var hmac = new HMACSHA256(key);
        byte[] hash = hmac.ComputeHash(Encoding.UTF8.GetBytes(data));
        return Convert.ToBase64String(hash);
    }
}
