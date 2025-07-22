type ButtonProps = {
    type: "submit" | "reset" | "button" | undefined;
    text: string;
    disabled: boolean;
    onClick?: (() => (void | Promise<void>)) | undefined;
};

const buttonClass = "mr-1 text-text-font p-2 rounded-lg"
const clickableButtonClass = "not-disabled:cursor-pointer transition disabled:bg-disabled-button"

export function SubmitButton({type, text, disabled, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} mb-2 font-semibold text-submit-button-text
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
                className={`${clickableButtonClass} ${buttonClass} mb-2 bg-nav-button text-nav-button-text font-semibold hover:bg-nav-button-hover`}
                onClick={onClick}
        >
            {text}
        </button>
    )
}

export function DeleteButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} mb-2 bg-delete-button text-delete-button-text font-semibold hover:bg-delete-button-hover`}
                onClick={onClick}
        >
            {text}
        </button>
    )
}

export function DisplayOnlyTagButton({type, text}: ButtonProps) {
    return (
        <button type={type}
                className={`${buttonClass} bg-tag-selected text-tag-text-selected`}
                disabled={true}
        >
            {text}
        </button>
    )
}

export function SelectedTagButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} bg-tag-selected text-tag-text-selected hover:bg-tag-deselected`}
                onClick={onClick ? () => onClick() : undefined}
        >
            {text}
        </button>
    )
}

export function DeselectedTagButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} bg-tag-deselected text-tag-text-deselected hover:bg-tag-selected`}
                onClick={onClick ? () => onClick() : undefined}
        >
            {text}
        </button>
    )
}
