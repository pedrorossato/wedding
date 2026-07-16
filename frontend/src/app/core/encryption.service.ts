const encoder = new TextEncoder();

export class EncryptionService {
  async encrypt(plaintext: string, keyBase64: string): Promise<string> {
    const keyBytes = await crypto.subtle.digest('SHA-256', encoder.encode(keyBase64));

    const key = await crypto.subtle.importKey('raw', keyBytes, 'AES-GCM', false, ['encrypt']);

    const iv = crypto.getRandomValues(new Uint8Array(12));

    const ciphertext = await crypto.subtle.encrypt(
      { name: 'AES-GCM', iv },
      key,
      encoder.encode(plaintext),
    );

    const combined = new Uint8Array(iv.length + ciphertext.byteLength);
    combined.set(iv, 0);
    combined.set(new Uint8Array(ciphertext), iv.length);

    return btoa(String.fromCharCode(...combined));
  }
}
