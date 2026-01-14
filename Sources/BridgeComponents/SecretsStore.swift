//
//  SecretsStore.swift
//  BridgeComponents
//
//  Created by Maximilian Babescu Local on 14.01.2026.
//

import Security
import Foundation

enum SecretsStoreError: Error {
    case noKeyInKeychain
    case invalidResponse
    case keychainFailure(status: OSStatus)
}

public class SecretsStore {
    private var keychainService: String {
        // Uses the host app's bundle identifier
        Bundle.main.bundleIdentifier ?? "com.imaximix.bridge_components"
    }
    
    func fetch(_ key: String) throws -> String {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: keychainService,
            kSecAttrAccount as String: key,
            kSecReturnData as String: true
        ]
        
        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        
        guard status != errSecItemNotFound else {
            throw SecretsStoreError.noKeyInKeychain
        }
        
        guard status == errSecSuccess,
              let data = item as? Data,
              let secret = String(data: data, encoding: .utf8) else {
            throw SecretsStoreError.invalidResponse
        }
        
        return secret
    }
    
    func put(_ key: String, _ secret: String) throws {
        guard let data = secret.data(using: .utf8) else {
            throw SecretsStoreError.invalidResponse
        }
        
        let baseQuery: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: keychainService,
            kSecAttrAccount as String: key
        ]
        
        let attributes: [String: Any] = [
            kSecValueData as String: data,
            kSecAttrAccessible as String: kSecAttrAccessibleAfterFirstUnlock
        ]
        
        let updateStatus = SecItemUpdate(baseQuery as CFDictionary, attributes as CFDictionary)
        if updateStatus == errSecItemNotFound {
            var addQuery = baseQuery
            addQuery[kSecValueData as String] = data
            addQuery[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlock
            let addStatus = SecItemAdd(addQuery as CFDictionary, nil)
            guard addStatus == errSecSuccess else { throw SecretsStoreError.keychainFailure(status: addStatus) }
        } else if updateStatus != errSecSuccess {
            throw SecretsStoreError.keychainFailure(status: updateStatus)
        }
    }
}
