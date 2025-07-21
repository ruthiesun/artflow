type InputProps = {
    label: string;
    type: string;
    value: string;
    setValue: ((newValue) => void)
};

export function Input({label, type, value, setValue}: InputProps) {
    return (
        <div className="mb-4">
            <label className="font-label block text-sm font-medium text-label">{label}</label>
            <input
                type={type}
                value={value}
                onChange={e => setValue(e.target.value)}
                className="font-input text-input-text mt-1 w-full px-4 py-2 border border-input-border rounded-lg focus:outline-none focus:ring-2 focus:ring-input-border-focus"
            />
        </div>
    )
}
