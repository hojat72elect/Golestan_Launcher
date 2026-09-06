import { NativeModules, Platform } from 'react-native';
import type { AppInfo } from '../types/app';

const { LauncherModule } = NativeModules;

export interface LauncherModuleInterface {
  getInstalledApps(): Promise<{ apps: string }>;
  launchApp(packageName: string, activityName: string): Promise<boolean>;
}

export const launcherModule: LauncherModuleInterface = LauncherModule;

export const getInstalledApps = async (): Promise<AppInfo[]> => {
  try {
    if (!LauncherModule) {
      console.error('LauncherModule is not available. Make sure the native module is properly linked.');
      return [];
    }
    const result = await launcherModule.getInstalledApps();
    const apps = JSON.parse(result.apps);
    return apps;
  } catch (error) {
    console.error('Error getting installed apps:', error);
    return [];
  }
};

export const launchApp = async (packageName: string, activityName: string): Promise<boolean> => {
  try {
    if (!LauncherModule) {
      console.error('LauncherModule is not available. Make sure the native module is properly linked.');
      return false;
    }
    return await launcherModule.launchApp(packageName, activityName);
  } catch (error) {
    console.error('Error launching app:', error);
    return false;
  }
};
