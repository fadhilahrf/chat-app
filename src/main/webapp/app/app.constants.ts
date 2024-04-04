// These constants are injected via webpack DefinePlugin variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application

declare const __DEBUG_INFO_ENABLED__: boolean;
declare const __VERSION__: string;

export const VERSION = __VERSION__;
export const DEBUG_INFO_ENABLED = __DEBUG_INFO_ENABLED__;

export class DeleteConfirmation {
    public static FOR_YOU_MESSAGE = 'Are you sure you want to delete this message just for you?';
    public static FOR_ALL_MESSAGE = 'Are you sure you want to delete this message for all?';
    public static FOR_YOU_ROOM: 'Are you sure you want to delete this chat just for you?';
    public static  FOR_ALL_ROOM: 'Are you sure you want to delete this chat for all?';
}

export class DeleteType {
    public static MESSAGE = 'MESSAGE';
    public static ROOM = 'ROOM';
}