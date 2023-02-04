interface Panels {
  [names: string]: [(() => Promise<void>) | null, (() => Promise<void>) | null];
}

export default class PanelManager {
  private readonly panels: Panels;

  private currentPanel: string | null;

  constructor() {
    this.panels = {};
    this.currentPanel = null;
  }

  registerPanel(
    name: string,
    renderCallback: (() => Promise<void>) | null,
    closeCallback: (() => Promise<void>) | null
  ) {
    if (
      Object.prototype.hasOwnProperty.call(this.panels, name) ||
      document.getElementById(name) === null
    )
      return;
    this.panels[name] = [renderCallback, closeCallback];
  }

  async switchToPanel(name: string) {
    if (
      this.currentPanel !== null &&
      (this.currentPanel === name ||
        !Object.prototype.hasOwnProperty.call(this.panels, name))
    )
      return;
    if (this.currentPanel !== null) {
      document.getElementById(this.currentPanel)?.classList.add("hidden");
      this.panels[this.currentPanel][1]?.().then();
    }
    this.currentPanel = name;
    if (Object.prototype.hasOwnProperty.call(this.panels, name)) {
      await this.panels[name][0]?.();
    }
    document.getElementById(name)?.classList.remove("hidden");
  }
}
