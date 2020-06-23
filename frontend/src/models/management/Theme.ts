import { UserItem } from '@/models/management/UserItem';

export class Theme {
  userItemDto!: UserItem;
  success!: string;
  secondary!: string;
  accent!: string;
  info!: string;
  warning!: string;
  primary!: string;
  error!: string;
  dark!: boolean;

  constructor(jsonObj?: Theme) {
    if (jsonObj) {
      if (jsonObj.userItemDto)
        this.userItemDto = new UserItem(jsonObj.userItemDto);
      this.success = jsonObj.success;
      this.secondary = jsonObj.secondary;
      this.accent = jsonObj.accent;
      this.info = jsonObj.info;
      this.warning = jsonObj.warning;
      this.primary = jsonObj.primary;
      this.error = jsonObj.error;
      this.dark = jsonObj.dark;
    }
  }
}
