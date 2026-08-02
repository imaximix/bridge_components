//
//  TabBarComponent.swift
//  BridgeComponents
//
//  Created by Maximilian Babescu Local on 10.01.2026.
//

import UIKit
import HotwireNative

open class TabBarComponent: BridgeComponent {
    
    override nonisolated static public var name: String { "tab-bar" }
    let identifier: UUID = UUID()
    
    private var tabBarController: HotwireTabBarController? {
        viewController?.tabBarController as? HotwireTabBarController
    }
    
    override public func onReceive(message: Message) {
        guard let data: MessageData = message.data() else { return }
        guard let tabBarController else { return }
        guard let rootURL = BridgeComponentsConfiguration.rootUrl else { return }
        
        if shouldReloadTabs(newTabs: data.tabs) {
            let hotwireTabs = data.tabs.map { tab in
                HotwireTab(id: tab.path, title: tab.name, image: UIImage(systemName: tab.imageName)!, url: rootURL.appendingPathComponent(tab.path))
            }
            
            tabBarController.load(hotwireTabs)
            tabBarController.tabBar.isHidden = data.tabBarHidden
        } else if data.refreshUnselectedTabs {
            NotificationCenter.default.post(name: NSNotification.Name("WebViewReload"), object: nil, userInfo: ["identifier": identifier])
        }
    }
    
    private func shouldReloadTabs(newTabs: [Tab]) -> Bool {
        guard let tabBarController else { return false }
        
        let countDifferent = if #available(iOS 18.0, *) {
            tabBarController.tabs.count != newTabs.count
        } else {
            tabBarController.viewControllers?.count != newTabs.count
        }
        
        let idsDifferent = if #available(iOS 18.0, *) {
            zip(tabBarController.tabs.map(\.identifier), newTabs.map(\.path)).contains(where: { $0 != $1 })
        } else {
            false
        }
        
        return countDifferent || idsDifferent
    }
    
}

private extension TabBarComponent {
    struct MessageData: Decodable {
        let tabs: [Tab]
        let tabBarHidden: Bool
        let refreshUnselectedTabs: Bool
    }
    
    struct Tab: Decodable {
        let name: String
        let path: String
        let imageName: String
        
        enum CodingKeys: String, CodingKey {
            case name, path
            case imageName = "ios_image"
        }
    }
}
