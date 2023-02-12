// eslint-disable-next-line import/no-unresolved
import { appWindow } from "@tauri-apps/api/window";
// eslint-disable-next-line import/no-unresolved
import { invoke, convertFileSrc } from "@tauri-apps/api/tauri";
import { open } from "@tauri-apps/api/dialog";
import PanelManager from "./panel_manager";
import formatDuration from "./utils";
import { Album, ConfigRepresentation, Infos, MusicInfos, Track } from "./jsons";
import MusicProgress from "./custom_elements/music_progress";

const panelManager = new PanelManager();
const titleBarClose: HTMLElement | null =
  document.getElementById("titlebar-close");
const titleBarMaximize: HTMLElement | null =
  document.getElementById("titlebar-maximize");
const titleBarMinimize: HTMLElement | null =
  document.getElementById("titlebar-minimize");
const musicProgress: MusicProgress = document.getElementById(
  "music-progress"
)! as MusicProgress;
const musicCover = document.getElementById("music-cover")! as HTMLImageElement;
const noMusicCover: HTMLElement = document.getElementById("no-music-cover")!;
const musicTitle = document.getElementById(
  "music-title"
)! as HTMLParagraphElement;
const musicSubtitle = document.getElementById(
  "music-subtitle"
)! as HTMLParagraphElement;
const soundProgressBar: HTMLInputElement = document.getElementById(
  "sound-progressbar"
)! as HTMLInputElement;
const playButton: HTMLElement | null = document.getElementById("play-button");
const nextButton: HTMLElement | null = document.getElementById("next-button");
const previousButton: HTMLElement | null =
  document.getElementById("previous-button");
const playButtonResume: HTMLElement =
  document.getElementById("play-button-resume")!;
const playButtonPause: HTMLElement =
  document.getElementById("play-button-pause")!;
const panelAlbumsList: HTMLElement =
  document.getElementById("panel-albums-list")!;
const panelAlbumsNoElements: HTMLElement = document.getElementById(
  "panel-albums-no-elements"
)!;
const panelTracksList: HTMLElement =
  document.getElementById("panel-tracks-list")!;
const panelTracksNoElements: HTMLElement = document.getElementById(
  "panel-tracks-no-elements"
)!;
const panelAlbumsTracksListContainer: HTMLElement = document.getElementById(
  "panel-albums-tracks-list-container"
)!;
const panelAlbumsTracksListAlbumName: HTMLElement = document.getElementById(
  "panel-albums-tracks-list-album-name"
)!;
const panelAlbumsTracksListAlbumCover = document.getElementById(
  "panel-albums-tracks-list-album-cover"
)! as HTMLImageElement;
const panelAlbumsTracksListAlbumYear: HTMLElement = document.getElementById(
  "panel-albums-tracks-list-album-year"
)!;
const panelAlbumsTracksListAlbumArtist: HTMLElement = document.getElementById(
  "panel-albums-tracks-list-album-artist"
)!;
const panelAlbumsTracksListCloseButton: HTMLElement | null =
  document.getElementById("panel-albums-tracks-list-close-button");
const leftMenuItemAlbums: HTMLElement = document.getElementById(
  "left-menu-item-albums"
)!;
const leftMenuSeparatorAlbums: HTMLElement = document.getElementById(
  "left-menu-separator-albums"
)!;
const leftMenuItemTracks: HTMLElement = document.getElementById(
  "left-menu-item-tracks"
)!;
const leftMenuSeparatorTracks: HTMLElement = document.getElementById(
  "left-menu-separator-tracks"
)!;
const leftMenuItemPlaylists: HTMLElement = document.getElementById(
  "left-menu-item-playlists"
)!;
const leftMenuSeparatorPlaylists: HTMLElement = document.getElementById(
  "left-menu-separator-playlists"
)!;
const leftMenuItemDownloader: HTMLElement = document.getElementById(
  "left-menu-item-downloader"
)!;
const soundButton: HTMLElement = document.getElementById("sound-button")!;
const soundButtonMute: HTMLElement =
  document.getElementById("sound-button-mute")!;
const soundButtonUnMute: HTMLElement = document.getElementById(
  "sound-button-unmute"
)!;
const settingsToggleWindowCustomTitlebar: HTMLInputElement =
  document.getElementById(
    "settings-toggle-window-custom-titlebar"
  )! as HTMLInputElement;
const settingsToggleWindowBackgroundPlaying: HTMLInputElement =
  document.getElementById(
    "settings-toggle-window-background-playing"
  )! as HTMLInputElement;
const settingsToggleLibraryShowPlaylists: HTMLInputElement =
  document.getElementById(
    "settings-toggle-library-show-playlists"
  )! as HTMLInputElement;
const settingsAddLibraryButton: HTMLElement = document.getElementById(
  "settings-add-library-button"
)!;
const settingsLibraryListContainer: HTMLElement = document.getElementById(
  "settings-library-list-container"
)!;

let leftMenuPreviousSelectedItem: HTMLElement = leftMenuItemAlbums;
let leftMenuPreviousSelectedSeparator: HTMLElement | null =
  leftMenuSeparatorAlbums;
let currentAlbumSelectedUUID: string | null = null;
let currentPlayerSoundUUID: string | null = null;
const infos: Infos = await invoke("infos", {});

titleBarClose?.addEventListener("click", () => appWindow.close());
titleBarMaximize?.addEventListener("click", () => appWindow.toggleMaximize());
titleBarMinimize?.addEventListener("click", () => appWindow.minimize());

const leftMenuSwitch = (
  element: HTMLElement,
  separator: HTMLElement | null
) => {
  if (leftMenuPreviousSelectedSeparator !== null) {
    leftMenuPreviousSelectedSeparator.classList.remove("hidden");
  }
  if (separator !== null) {
    separator.classList.add("hidden");
  }

  leftMenuPreviousSelectedItem.classList.remove(
    "left-menu-selection-selected",
    "left-menu-selection",
    "mb-2"
  );
  element.classList.add(
    "left-menu-selection-selected",
    "left-menu-selection",
    "mb-2"
  );

  leftMenuPreviousSelectedItem = element;
  leftMenuPreviousSelectedSeparator = separator;
};

document.getElementById("open-settings")!.addEventListener("click", () => {
  panelManager.switchToPanel("panel-settings").then();
});
leftMenuItemAlbums?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemAlbums) return;

  panelManager.switchToPanel("panel-albums").then();
  leftMenuSwitch(leftMenuItemAlbums, leftMenuSeparatorAlbums);
});
leftMenuItemTracks?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemTracks) return;

  panelManager.switchToPanel("panel-tracks").then();
  leftMenuSwitch(leftMenuItemTracks, leftMenuSeparatorTracks);
});
leftMenuItemPlaylists?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemPlaylists) return;

  panelManager.switchToPanel("panel-in-dev").then();
  leftMenuSwitch(leftMenuItemPlaylists, leftMenuSeparatorPlaylists);
});
leftMenuItemDownloader?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemDownloader) return;

  panelManager.switchToPanel("panel-in-dev").then();
  leftMenuSwitch(leftMenuItemDownloader, null);
});

panelAlbumsTracksListCloseButton?.addEventListener("click", async () => {
  await panelManager.switchToPanel("panel-albums");
});

soundButton?.addEventListener("click", async () => {
  if (soundButtonUnMute.classList.contains("hidden")) {
    const setVolumeResult: boolean = await invoke("set_volume", { volume: 0 });
    if (!setVolumeResult) return;
    soundProgressBar.value = "0";
    soundButtonMute.classList.add("hidden");
    soundButtonUnMute.classList.remove("hidden");
  } else {
    // TODO: change for previous volume
    const setVolumeResult: boolean = await invoke("set_volume", { volume: 50 });
    if (!setVolumeResult) return;

    soundProgressBar.value = "50";
    soundButtonUnMute.classList.add("hidden");
    soundButtonMute.classList.remove("hidden");
  }
});

musicProgress?.addEventListener(
  "music-progress-change",
  async (event: Event) => {
    if ((<CustomEvent>event).detail === null) return;

    await invoke("set_progress", {
      time: parseInt((<CustomEvent>event).detail, 10),
    });
  }
);

const initSoundInfos = (res: MusicInfos) => {
  if (res.file_infos.length === 2) {
    if (res.file_infos[0].uuid !== currentPlayerSoundUUID) {
      if (
        musicCover !== null &&
        res.file_infos[1] !== null &&
        res.file_infos[1].cover === 1
      ) {
        musicCover.src = convertFileSrc(
          `${infos.covers_path}/${res.file_infos[1].uuid}.png`
        );
        noMusicCover?.classList.add("hidden");
        musicCover.classList.remove("hidden");
      } else {
        musicCover.classList.add("hidden");
        noMusicCover?.classList.remove("hidden");
      }

      if (musicSubtitle !== null && res.file_infos[1] !== null) {
        musicSubtitle.textContent = `${res.file_infos[1].artist} - ${res.file_infos[1].name}`;
      }
    }
    currentPlayerSoundUUID = res.file_infos[0].uuid;
  } else {
    currentPlayerSoundUUID = null;
    musicCover.classList.add("hidden");
    noMusicCover?.classList.remove("hidden");
  }

  if (musicTitle !== null) {
    musicTitle.textContent = res.file_infos[0].title;
  }
};

const soundInterval = async () => {
  const currentInfos: MusicInfos = await invoke("sound_infos", {});
  if (currentInfos !== null && currentInfos !== undefined) {
    if (currentInfos.paused) {
      playButtonPause.classList.add("hidden");
      playButtonResume.classList.remove("hidden");
    } else {
      playButtonResume.classList.add("hidden");
      playButtonPause.classList.remove("hidden");
    }
    musicProgress.setMaxValue(currentInfos.duration.secs);
    musicProgress.classList.remove("hidden");
    musicProgress.setCurrentProgress(currentInfos.progress.secs);

    initSoundInfos(currentInfos);
  } else {
    musicProgress.classList.add("hidden");
    musicProgress.setCurrentProgress(0);
    musicCover.classList.add("hidden");
    noMusicCover?.classList.remove("hidden");
    musicTitle.textContent = "";
    musicSubtitle.textContent = "";
  }
};

nextButton?.addEventListener("click", async () => {
  await invoke("next", {});
});

previousButton?.addEventListener("click", async () => {
  await invoke("previous", {});
});

playButton?.addEventListener("click", async () => {
  if (playButtonResume.classList.contains("hidden")) {
    const result: boolean = await invoke("pause", {});
    if (result) {
      playButtonPause.classList.add("hidden");
      playButtonResume.classList.remove("hidden");
    }
  } else {
    const result: boolean = await invoke("resume", {});
    if (result) {
      playButtonResume.classList.add("hidden");
      playButtonPause.classList.remove("hidden");
    }
  }
});

soundProgressBar?.addEventListener("input", async (event: Event) => {
  if (event.target === null) return;
  const target = event.target as HTMLInputElement;

  if (parseInt(target.value, 10) < 101 && parseInt(target.value, 10) >= 0) {
    await invoke("set_volume", { volume: parseInt(target.value, 10) });
  }
});

const removeLibraryElement = async (path: string) => {
  await invoke("set_setting", {
    value: {
      property: "library_paths_delete",
      value: path,
    },
  });

  const element = document.querySelector(
    `.settings-library-list-element[path="${path}"]`
  ) as HTMLElement;
  element.remove();
};

const generateLibrairiesList = (librairiesList: [string]) => {
  const elementAlreadyPresent: string[] = [];
  // eslint-disable-next-line no-restricted-syntax
  for (const fetchedElement of settingsLibraryListContainer.getElementsByClassName(
    "settings-library-list-element"
  )) {
    const element: HTMLElement = fetchedElement as HTMLElement;
    const path: string | null = element.getAttribute("path");
    if (path !== null) {
      elementAlreadyPresent.push(path);
    }
  }

  // let htmlToAdd = "";
  // eslint-disable-next-line no-restricted-syntax
  for (const element of librairiesList) {
    // eslint-disable-next-line no-continue
    if (elementAlreadyPresent.includes(element)) continue;
    const settingsLibraryListElement = document.createElement("div");
    settingsLibraryListElement.classList.add(
      "flex",
      "justify-between",
      "h-12",
      "bg-dark-celeria",
      "rounded-md",
      "items-center",
      "settings-library-list-element"
    );
    settingsLibraryListElement.setAttribute("path", element);

    settingsLibraryListElement.innerHTML = `
        <h2 class="text-lg text-white-1 text-mukta ml-4">${element}</h2>
        <div class="flex flex-row space-x-2 mr-4">
          <div class="cursor-pointer hover:bg-dark-celeria-2 rounded-md w-8 h-8 flex justify-center items-center settings-library-list-element-open">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-white-1">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 9.776c.112-.017.227-.026.344-.026h15.812c.117 0 .232.009.344.026m-16.5 0a2.25 2.25 0 00-1.883 2.542l.857 6a2.25 2.25 0 002.227 1.932H19.05a2.25 2.25 0 002.227-1.932l.857-6a2.25 2.25 0 00-1.883-2.542m-16.5 0V6A2.25 2.25 0 016 3.75h3.879a1.5 1.5 0 011.06.44l2.122 2.12a1.5 1.5 0 001.06.44H18A2.25 2.25 0 0120.25 9v.776" />
            </svg>
          </div>
          <div class="cursor-pointer hover:bg-dark-celeria-2 rounded-md w-8 h-8 flex justify-center items-center settings-library-list-element-delete">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-white-1">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
        </div>
    `;
    const deleteButton: HTMLElement = settingsLibraryListElement.querySelector(
      ".settings-library-list-element-delete"
    )! as HTMLElement;
    deleteButton.onclick = async () => {
      await removeLibraryElement(element);
    };

    const openButton: HTMLElement = settingsLibraryListElement.querySelector(
      ".settings-library-list-element-open"
    )! as HTMLElement;
    openButton.onclick = async () => {
      console.log(element);
      await invoke("open_in_folder", {
        path: element,
      });
    };
    settingsLibraryListContainer.appendChild(settingsLibraryListElement);
  }
};

setInterval(() => soundInterval(), 500);

panelManager.registerPanel(
  "panel-albums",
  async () => {
    const fetchAlbums: [Album] = await invoke("list_albums", {});
    if (fetchAlbums.length < 1) {
      panelAlbumsList.classList.add("hidden");
      panelAlbumsNoElements.classList.remove("hidden");
      return;
    }
    panelAlbumsNoElements.classList.add("hidden");
    panelAlbumsList.classList.remove("hidden");

    let htmlToAdd = "";

    // eslint-disable-next-line no-restricted-syntax
    for (const album of fetchAlbums) {
      const addHiddenForArtist = album.artist === null ? " hidden" : "";
      const addCover =
        album.cover === 0
          ? ""
          : `<img class="w-44 h-44 rounded-lg pt-2" src="${convertFileSrc(
              `${infos.covers_path}/${album.uuid}.png`
            )}">`;
      const addHiddenForDiv = album.cover === 1 ? " hidden" : "";

      htmlToAdd += `<div class="bg-dark-celeria w-[15rem] hover:w-[32rem] duration-500 ease-in-out h-80 p-8 rounded-sm group cursor-pointer m-4 album-element" uuid="${album.uuid}">
        ${addCover}
        <div class="w-44 h-44 rounded-lg bg-white-3${addHiddenForDiv}"></div>
        <div class="w-[10rem] group-hover:w-[25rem] duration-500 ease-in-out h-[7rem]">
          <h2 class="text-white-1 text-mukta text-2xl break-properly font-bold pt-2">${album.name}</h2>
          <h3 class="pt-1 text-white-1 text-mukta break-properly text-xl${addHiddenForArtist}">${album.artist}</h3>
        </div>
    </div>`;
    }
    panelAlbumsList.innerHTML = htmlToAdd;

    // eslint-disable-next-line no-restricted-syntax
    for (const fetchedElement of document.querySelectorAll(".album-element")) {
      const element: HTMLElement = fetchedElement as HTMLElement;

      // eslint-disable-next-line no-loop-func
      element.onclick = async () => {
        currentAlbumSelectedUUID = element.getAttribute("uuid");
        await panelManager.switchToPanel("panel-albums-tracks-list");
      };
    }
  },
  null
);

panelManager.registerPanel(
  "panel-albums-tracks-list",
  async () => {
    const albumInfos: [Album, [Track]] = await invoke("album_infos", {
      uuid: currentAlbumSelectedUUID,
    });

    panelAlbumsTracksListAlbumCover.src = convertFileSrc(
      `${infos.covers_path}/${albumInfos[0].uuid}.png`
    );
    panelAlbumsTracksListAlbumName.innerText = albumInfos[0].name;
    if (albumInfos[0].artist !== null) {
      panelAlbumsTracksListAlbumArtist.innerText = albumInfos[0].artist;
    }
    if (albumInfos[0].year !== null) {
      panelAlbumsTracksListAlbumYear.innerText = albumInfos[0].year.toString();
    }

    let htmlToAdd = "";
    // eslint-disable-next-line no-restricted-syntax
    for (const [i, track] of albumInfos[1].entries()) {
      htmlToAdd += `<div class="flex flex-row justify-between hover:bg-dark-celeria cursor-pointer p-3 rounded-lg album-track-element" uuid="${
        track.uuid
      }">
        <div class="flex items-center flex-row space-x-4">
            <span class="text-mukta text-md text-white-1 w-6 flex justify-end">${
              i + 1
            }</span>
            <p class="text-mukta text-lg text-white-1">${track.title}</p>
        </div>
        <div class="flex items-center flex-row space-x-6 pr-2">
            <p class="text-mukta text-lg text-white-1 opacity-80">${formatDuration(
              track.duration
            )}</p>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-white-1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM12.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM18.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" />
            </svg>
        </div>
    </div>`;
    }
    panelAlbumsTracksListContainer.innerHTML = htmlToAdd;
    // eslint-disable-next-line no-restricted-syntax
    for (const fetchedElement of document.querySelectorAll(
      ".album-track-element"
    )) {
      const element: HTMLElement = fetchedElement as HTMLElement;

      element.onclick = () => {
        invoke("play_sound", {
          trackUuid: element.getAttribute("uuid"),
        })
          .then()
          .catch();
      };
    }
  },
  null
);
panelManager.registerPanel(
  "panel-tracks",
  async () => {
    const fetchTracks: [Track] = await invoke("list_tracks", {});
    if (fetchTracks.length < 1) {
      panelTracksList.classList.add("hidden");
      panelTracksNoElements.classList.remove("hidden");
      return;
    }
    panelTracksNoElements.classList.add("hidden");
    panelTracksList.classList.remove("hidden");

    let htmlToAdd = "";

    // eslint-disable-next-line no-restricted-syntax
    for (const [i, track] of fetchTracks.entries()) {
      let { title } = track;
      if (title === null) {
        title = "Inconnu";
      }

      htmlToAdd += `<div class="flex flex-row justify-between hover:bg-dark-celeria cursor-pointer p-3 rounded-lg track-element" uuid="${
        track.uuid
      }">
        <div class="flex items-center flex-row space-x-4">
            <span class="text-mukta text-md text-gray-300 w-6 flex justify-end">${
              i + 1
            }</span>
            <p class="text-mukta text-lg text-white-1">${title}</p>
        </div>
        <div class="flex items-center flex-row space-x-6 pr-2">
            <p class="text-mukta text-lg text-white-1 opacity-80">${formatDuration(
              track.duration
            )}</p>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-white-1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM12.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM18.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" />
            </svg>
        </div>
    </div>`;
    }

    panelTracksList.innerHTML = htmlToAdd;

    // eslint-disable-next-line no-restricted-syntax
    for (const fetchedElement of document.querySelectorAll(".track-element")) {
      const element: HTMLElement = fetchedElement as HTMLElement;

      // eslint-disable-next-line no-loop-func
      element.onclick = async () => {
        invoke("play_sound", {
          trackUuid: element.getAttribute("uuid"),
        })
          .then()
          .catch();
      };
    }
  },
  null
);
panelManager.registerPanel(
  "panel-settings",
  async () => {
    const getSettings: ConfigRepresentation = await invoke("get_settings", {});

    settingsToggleWindowCustomTitlebar.checked =
      getSettings.window.custom_titlebar;
    settingsToggleWindowBackgroundPlaying.checked =
      getSettings.window.keep_running_background;
    settingsToggleLibraryShowPlaylists.checked =
      getSettings.window.keep_running_background;
    generateLibrairiesList(getSettings.library.paths);

    settingsToggleWindowCustomTitlebar.onclick = async () => {
      await invoke("set_setting", {
        value: {
          property: "window_custom_titlebar",
          value: settingsToggleWindowCustomTitlebar.checked,
        },
      });
    };
    settingsToggleWindowBackgroundPlaying.onclick = async () => {
      await invoke("set_setting", {
        value: {
          property: "window_keep_running_background",
          value: settingsToggleWindowBackgroundPlaying.checked,
        },
      });
    };
    settingsToggleLibraryShowPlaylists.onclick = async () => {
      await invoke("set_setting", {
        value: {
          property: "library_show_playlists",
          value: settingsToggleLibraryShowPlaylists.checked,
        },
      });
    };
    settingsAddLibraryButton.onclick = async () => {
      const selected: string | string[] | null = await open({
        directory: true,
        multiple: true,
      });
      if (Array.isArray(selected)) {
        let newLibraryList: null | [string] = null;

        // eslint-disable-next-line no-restricted-syntax
        for (const element of selected) {
          // eslint-disable-next-line no-await-in-loop
          newLibraryList = await invoke("set_setting", {
            value: {
              property: "library_paths_add",
              value: element,
            },
          });
        }
        if (newLibraryList !== null) {
          generateLibrairiesList(newLibraryList);
        }
      } else if (selected !== null) {
        const newLibraryList: null | [string] = await invoke("set_setting", {
          value: {
            property: "library_paths_add",
            value: selected,
          },
        });
        if (newLibraryList !== null) {
          generateLibrairiesList(newLibraryList);
        }
      }
    };
  },
  async () => {
    settingsToggleWindowCustomTitlebar.onclick = null;
    settingsToggleWindowBackgroundPlaying.onclick = null;
    settingsToggleLibraryShowPlaylists.onclick = null;
    settingsAddLibraryButton.onclick = null;
  }
);

panelManager.registerPanel("panel-in-dev", null, null);

panelManager.switchToPanel("panel-albums").then();
