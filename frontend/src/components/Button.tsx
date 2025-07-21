type ButtonProps = {
    type: "submit" | "reset" | "button" | undefined;
    text: string;
    disabled: boolean;
    onClick: (() => (void | Promise<void>)) | undefined;
};

const buttonClass = "text-text-font not:disabled:cursor-pointer p-2 rounded-lg transition disabled:bg-disabled-button"
const tagButtonClass = "mr-1"

export function SubmitButton({type, text, disabled, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${buttonClass} mb-2 font-semibold text-submit-button-text 
                bg-submit-button hover:bg-submit-button-hover`}
                onClick={onClick}
                disabled={disabled}
        >
            {text}
        </button>
    )
}

export function NavButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${buttonClass} mb-2 bg-nav-button text-nav-button-text font-semibold hover:bg-nav-button-hover`}
                onClick={onClick}
        >
            {text}
        </button>
    )
}

export function SelectedTagButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${buttonClass} ${tagButtonClass} bg-tag-selected text-tag-text-selected hover:bg-tag-deselected`}
                onClick={onClick ? () => onClick() : undefined}
        >
            {text}
        </button>
    )
}

export function DeselectedTagButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${buttonClass} ${tagButtonClass} bg-tag-deselected text-tag-text-deselected hover:bg-tag-selected`}
                onClick={onClick ? () => onClick() : undefined}
        >
            {text}
        </button>
    )
}
