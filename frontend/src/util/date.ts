// date: string of format YYYY-MM-DD
// returns dateTime in ISO format, UTC time (12 AM)
export function getDateTimeStringUTC(date: string): string | null {
    return date === "" ? null : (new Date(date + "T00:00:00")).toISOString();
}

// dateTime: string in ISO format, UTC time
// returns date and time in local time
export function getDateTimeStringLocal(dateTime: string): string {
    return getDateStringLocal(dateTime) + " " + getTimeStringLocal(dateTime);
}

// dateTime: string in ISO format, UTC time
// returns local date
export function getDateStringLocal(dateTime: string): string {
    const date: Date = new Date(dateTime);
    return date.toLocaleDateString();
}

// dateTime: string in ISO format, UTC time
// returns local time
export function getTimeStringLocal(dateTime: string): string {
    const date: Date = new Date(dateTime);
    return date.toLocaleTimeString();
}

export function toISO(dateTime: string): string {
    return dateTime.substring(0, 19) + "Z";
}