import formatDuration from "../utils";

export default class MusicProgress extends HTMLElement {
  private progressBarDiv: HTMLElement;

  private mouseDown: boolean;

  private minProgress: number;

  private maxProgress: number;

  private currentProgress: number;

  private realCurrentProgress: number;

  private progressIndicatorTimeout: ReturnType<typeof setTimeout> | null;

  constructor() {
    super();
    this.mouseDown = false;
    this.progressIndicatorTimeout = null;
    this.realCurrentProgress = 0;

    const getMinValue = this.getAttribute("min-value");
    if (getMinValue !== null) {
      this.minProgress = parseInt(getMinValue, 10);
    } else {
      this.minProgress = 100;
    }

    const getMaxValue = this.getAttribute("max-value");
    if (getMaxValue !== null) {
      this.maxProgress = parseInt(getMaxValue, 10);
    } else {
      this.maxProgress = 100;
    }

    const getCurrentProgress = this.getAttribute("progress");
    if (getCurrentProgress !== null) {
      this.currentProgress = parseInt(getCurrentProgress, 10);
    } else {
      this.currentProgress = 100;
    }

    const progressIndicator = document.createElement("div");
    progressIndicator.classList.add(
      "bg-dark-celeria",
      "pt-1",
      "pb-1",
      "pl-2",
      "pr-2",
      "text-white-1",
      "text-mukta",
      "absolute",
      "hidden",
      "rounded-md",
      "flex",
      "justify-center",
      "items-center",
      "hidden"
    );
    this.appendChild(progressIndicator);

    const progressBarContainer = document.createElement("div");
    progressBarContainer.classList.add("h-2");

    const progressBarDiv = document.createElement("div");
    progressBarDiv.classList.add(
      "h-1",
      "w-full",
      "duration-500",
      "ease-in-out"
    );
    progressBarDiv.style.background = "#1f6267";
    this.progressBarDiv = progressBarDiv;

    progressBarContainer.addEventListener("mousedown", (e) => {
      progressBarDiv.classList.remove("duration-500");
      this.mouseDown = true;
      progressIndicator.classList.add("hidden");
      progressBarDiv.style.width = `${
        progressBarDiv.getBoundingClientRect().width +
        (e.pageX - this.progressBarDiv.getBoundingClientRect().right)
      }px`;
      this.updatePercentage(progressBarContainer, progressIndicator);
      this.setAttribute(
        "progress",
        Math.floor(
          this.currentProgress * (this.maxProgress - this.minProgress) +
            this.minProgress
        ).toString(10)
      );
      progressIndicator.style.transform = `translate(${
        this.progressBarDiv.getBoundingClientRect().width -
        progressIndicator.getBoundingClientRect().width / 2 -
        20
      }px, -36px)`;
    });

    progressBarContainer.addEventListener("mouseenter", () => {
      this.updatePercentage(progressBarContainer, progressIndicator);

      progressIndicator.style.transform = `translate(${
        this.progressBarDiv.getBoundingClientRect().width -
        progressIndicator.getBoundingClientRect().width / 2 -
        20
      }px, -36px)`;
      this.progressIndicatorTimeout = setTimeout(() => {
        progressIndicator.classList.remove("hidden");
      }, 120);
    });

    progressBarContainer.addEventListener("mouseleave", () => {
      if (this.progressIndicatorTimeout !== null) {
        clearTimeout(this.progressIndicatorTimeout);
      }
      this.progressIndicatorTimeout = setTimeout(() => {
        progressIndicator.classList.add("hidden");
      }, 10);
    });

    document.addEventListener("mousemove", (e) => {
      if (!this.mouseDown) return;

      if (this.progressIndicatorTimeout !== null) {
        clearTimeout(this.progressIndicatorTimeout);
      }

      progressBarDiv.style.width = `${
        progressBarDiv.getBoundingClientRect().width +
        (e.pageX - progressBarDiv.getBoundingClientRect().right)
      }px`;
      progressIndicator.style.transform = `translate(${
        this.progressBarDiv.getBoundingClientRect().width -
        progressIndicator.getBoundingClientRect().width / 2 -
        20
      }px, -36px)`;
      this.updatePercentage(progressBarContainer, progressIndicator);
      this.setAttribute(
        "progress",
        Math.floor(
          this.currentProgress * (this.maxProgress - this.minProgress) +
            this.minProgress
        ).toString(10)
      );
    });

    document.addEventListener("mouseup", () => {
      if (!this.mouseDown) return;

      const event = new CustomEvent("music-progress-change", {
        detail: this.realCurrentProgress,
      });
      this.dispatchEvent(event);
      progressBarDiv.classList.add("duration-500");
      this.mouseDown = false;
      progressIndicator.classList.remove("hidden");
    });

    progressBarContainer.appendChild(progressBarDiv);
    this.appendChild(progressBarContainer);

    window.addEventListener("resize", () => {
      progressBarDiv.classList.add("duration-500");
      progressBarDiv.style.width = `${
        this.currentProgress * this.getBoundingClientRect().width
      }px`;
      progressIndicator.style.transform = `translate(${
        this.progressBarDiv.getBoundingClientRect().width -
        progressIndicator.getBoundingClientRect().width / 2 -
        20
      }px, -36px)`;
    });
  }

  private updatePercentage(
    progressBarContainer: HTMLElement,
    progressIndicator: HTMLElement
  ) {
    const currentProgress =
      this.progressBarDiv.getBoundingClientRect().width /
      progressBarContainer.getBoundingClientRect().width;
    this.currentProgress = currentProgress;
    progressIndicator.innerText = formatDuration(
      Math.floor(
        currentProgress * (this.maxProgress - this.minProgress) +
          this.minProgress
      )
    );
  }

  setCurrentProgress(value: number) {
    this.setAttribute("progress", value.toString(10));
  }

  setMaxValue(value: number) {
    this.setAttribute("max-value", value.toString(10));
  }

  static get observedAttributes() {
    return ["min-value", "max-value", "suffix", "progress"];
  }

  attributeChangedCallback(name: string, _: object, newValue: object) {
    if (newValue === null) return;

    if (name === "min-value") {
      this.minProgress = parseInt(newValue.toString(), 10);
    } else if (name === "max-value") {
      this.maxProgress = parseInt(newValue.toString(), 10);
    } else if (name === "progress") {
      this.realCurrentProgress = parseInt(newValue.toString(), 10);
      const currentProgress =
        ((parseInt(newValue.toString(), 10) - this.minProgress) /
          (this.maxProgress - this.minProgress)) *
        100;
      const newProgress = currentProgress / 100;

      if (newProgress !== this.currentProgress) {
        this.currentProgress = newProgress;
        this.progressBarDiv.style.width = `${currentProgress}%`;
      }
    }
  }
}

customElements.define("music-progress", MusicProgress);
