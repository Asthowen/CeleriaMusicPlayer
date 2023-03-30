export interface Duration {
  nanos: number;
  secs: number;
}

export interface Infos {
  covers_path: string;
}

export interface Album {
  uuid: string;
  name: string;
  artist: string | null;
  year: number | null;
  cover: number;
}

export interface Track {
  uuid: string;
  title: string | null;
  album: string | null;
  duration: number;
  file_path: string;
}

export interface MusicInfos {
  paused: boolean;
  duration: Duration;
  started_at: Duration;
  progress: Duration;
  remain_time: Duration;
  file_infos: [Track, Album | null];
}

export interface ConfigWindow {
  custom_titlebar: boolean;
  keep_running_background: boolean;
}

export interface ConfigLibrary {
  paths: [string];
  show_playlists: boolean;
}

export interface ConfigRepresentation {
  language: string;
  library: ConfigLibrary;
  window: ConfigWindow;
}

export interface MusicQueue {
  previous: [Track, Album | null][];
  next: [Track, Album | null][];
}
