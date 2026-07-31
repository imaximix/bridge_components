import TabBarController from "./tab_bar_controller.js";
import SecretsStoreController from "./secrets_store_controller.js";
import ButtonController from "./button_controller.js";

export {
  TabBarController,
  SecretsStoreController,
  ButtonController
};

export const controllers = [
  { identifier: "tab-bar",  controllerConstructor: TabBarController },
  { identifier: "secrets-store", controllerConstructor: SecretsStoreController },
  { identifier: "button", controllerConstructor: ButtonController }
];