import Foundation

@MainActor
public final class BridgeComponentsConfiguration {
    private init() {}
    
    public static var rootUrl: URL?
    
    public static func configureTabBarComponent(
        rootUrl: URL,
    ) {
        self.rootUrl = rootUrl
    }
}
