export interface Duration {
  nanos: number;
  secs: number;
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

export interface ListAlbums {
  list: [Album];
  pictures_path: string;
}

export interface MusicInfos {
  paused: boolean;
  duration: Duration;
  started_at: Duration;
  progress: Duration;
  remain_time: Duration;
  file_infos: [Track, Album | null];
  cover_path: string;
}
