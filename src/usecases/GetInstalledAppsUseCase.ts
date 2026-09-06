import type {AppInfo} from "../domain/AppInfo";
import {LauncherModule, launcherModule} from "../modules/LauncherModule";

export async function GetInstalledAppsUseCase(): Promise<AppInfo[]> {
    try {
        if (!LauncherModule) {
            console.error('LauncherModule is not available. Make sure the native module is properly linked.');
            return [];
        }
        const result = await launcherModule.getInstalledApps();
        return JSON.parse(result.apps);
    } catch (error) {
        console.error('Error getting installed apps:', error);
        return [];
    }
}