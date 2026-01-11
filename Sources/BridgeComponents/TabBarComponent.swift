//
//  TabBarComponent.swift
//  BridgeComponents
//
//  Created by Maximilian Babescu Local on 10.01.2026.
//

import UIKit
import HotwireNative

class TabBarComponent: BridgeComponent {
    
    override nonisolated static var name: String { "tab-bar" }
    let identifier: UUID = UUID()
    
    private var viewController: UIViewController? {
        delegate?.destination as? UIViewController
    }
    
    private var tabBarController: HotwireTabBarController? {
        viewController?.tabBarController as? HotwireTabBarController
    }
    
    override func onReceive(message: Message) {
        guard let data: MessageData = message.data() else { return }
        guard let tabBarController else { return }
        guard let rootURL = BridgeComponentsConfiguration.rootUrl else { return }
        
        if tabBarController.viewControllers?.count != data.tabs.count {
            let hotwireTabs = data.tabs.map { tab in
                HotwireTab(title: tab.name, image: UIImage(systemName: tab.imageName)!, url: rootURL.appendingPathComponent(tab.path))
            }
            
            tabBarController.load(hotwireTabs)
            tabBarController.tabBar.isHidden = data.tabBarHidden
        } else if data.refreshUnselectedTabs {
            NotificationCenter.default.post(name: NSNotification.Name("WebViewReload"), object: nil, userInfo: ["identifier": identifier])
        }
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
