#include <iostream>
#include <openssl/aes.h>
#include <chrono>

void encrypt_aes(const unsigned char* input, unsigned char* output, AES_KEY* key) {
    AES_encrypt(input, output, key);
}

int main() {
    unsigned char key[16] = {0}; // 128-bit key
    unsigned char input[16] = {0}; // Input data
    unsigned char output[16] = {0};

    AES_KEY enc_key;
    AES_set_encrypt_key(key, 128, &enc_key);

    auto start = std::chrono::high_resolution_clock::now();
    for (int i = 0; i < 1000; ++i) { // Simulasi 1000 kali enkripsi
        encrypt_aes(input, output, &enc_key);
    }
    auto end = std::chrono::high_resolution_clock::now();

    std::chrono::duration<double> elapsed = end - start;
    std::cout << "Waktu eksekusi: " << elapsed.count() << " detik\n";
    return 0;
}
