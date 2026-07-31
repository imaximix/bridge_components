//
//  BridgeComponent.swift
//  BridgeComponents
//
//  Created by Maximilian Babescu Local on 31.07.2026.
//

import HotwireNative
import UIKit

extension BridgeComponent {
    var viewController: UIViewController? {
        delegate?.destination as? UIViewController
    }
}
