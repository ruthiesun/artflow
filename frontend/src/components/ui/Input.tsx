export const labelClass = "font-label block text-sm font-medium text-secondary";
const textInputClass = "font-input text-foreground mt-1 w-full px-4 py-2 border border-secondary rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-secondary placeholder:text-muted placeholder:italic";

type InputProps = {
    label: string;
    type: string;
    value: string;
    setValue: ((newValue) => void)
    placeholder?: string;
    inKeyDown?: () => void;
};

export function Input({label, type, value, setValue, placeholder, onKeyDown}: InputProps) {
    return (
        <div className="mb-4">
            <label className={labelClass}>{label}</label>
            <input
                type={type}
                value={value}
                onChange={e => setValue(e.target.value)}
                placeholder={placeholder}
                onKeyDown={onKeyDown}
                className={textInputClass}
            />
        </div>
    )
}

type RadioInputProps = {
    label: string;
    name: string;
    value: string;
    onChange: (() => void);
    checked: boolean;
};
export function RadioInput({label, name, value, onChange, checked}: InputProps) {
    return (
            <label className={labelClass}>
                <input type="radio" name={name} value={value} checked={checked} onChange={onChange}
                className="text-foreground mr-1" />
                {label}
            </label>
    )
}

export function TextAreaInput({label, type, value, setValue, placeholder, onKeyDown}: InputProps) {
    return (
        <div className="mb-4">
            <label className={labelClass}>{label}</label>
            <textarea
                type={type}
                value={value}
                onChange={e => setValue(e.target.value)}
                placeholder={placeholder}
                onKeyDown={onKeyDown}
                rows="5"
                className={textInputClass}
            />
        </div>
    )
}

export function DateInput({label, type, value, setValue, placeholder, onKeyDown}: InputProps) {
    return (
        <div className="mb-4">
            <label className={labelClass}>{label}</label>
            <input
                type={type}
                value={value}
                onChange={e => setValue(e.target.value)}
                placeholder={placeholder}
                onKeyDown={onKeyDown}
                className={textInputClass}
            />
        </div>
    )
}