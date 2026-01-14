//
//  SecretsStoreComponent.swift
//  BridgeComponents
//
//  Created by Maximilian Babescu Local on 14.01.2026.
//

import UIKit
import HotwireNative

open class SecretsStoreComponent: BridgeComponent {
    override nonisolated static public var name: String { "secrets-store" }
    
    override public func onReceive(message: Message) {
        switch message.event {
        case "connect":
            guard let data: ConnectMessageData = message.data() else { return }
            handleConnect(data: data)
        case "store_secret":
            guard let data: StoreSecretMessageData = message.data() else { return }
            handleStoreSecret(data: data)
        default:
            print("Got unmanaged event: \(message.event)")
        }
    }
    
    fileprivate func handleConnect(data: ConnectMessageData) {
        do {
            let secret = try SecretsStore().fetch("default")
            reply(to: "connect", with: "{ \"secret\":  \"\(secret)\" }")
        } catch {
            print("Error fetching secret \(data.key) from keychain")
        }
    }
    
    fileprivate func handleStoreSecret(data: StoreSecretMessageData) {
        do {
            return try SecretsStore().put(data.key, data.secret)
        } catch {
            print("Error storing secret \(data.key) in keychain")
            return
        }
    }
}

private extension SecretsStoreComponent {
    struct ConnectMessageData: Decodable {
        let key: String
    }
    
    struct StoreSecretMessageData: Decodable {
        let secret: String
        let key: String
    }
}
