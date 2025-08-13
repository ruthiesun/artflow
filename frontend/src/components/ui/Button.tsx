type ButtonProps = {
    type: "submit" | "reset" | "button" | undefined;
    text: string;
    disabled?: boolean;
    onClick?: (() => (void | Promise<void>)) | undefined;
};

const buttonClass = "mr-1 text-text-font p-2 rounded-lg text-muted-100"
const clickableButtonClass = "cursor-pointer transition disabled:bg-muted disabled:cursor-default"
const normalButtonClass = "mb-2 font-semibold "

export function PrimaryButton({type, text, disabled, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} ${normalButtonClass} bg-primary hover:bg-primary-400`}
                onClick={onClick}
                disabled={disabled}
        >
            {text}
        </button>
    )
}

export function SecondaryButton({type, text, disabled, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} ${normalButtonClass} bg-secondary hover:bg-secondary-400`}
                onClick={onClick}
                disabled={disabled}
        >
            {text}
        </button>
    )
}

export function DeleteButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} mb-2 font-semibold bg-danger`}
                onClick={onClick}
        >
            {text}
        </button>
    )
}

export function DisplayOnlyTagButton({type, text}: ButtonProps) {
    return (
        <button type={type}
                className={`${buttonClass} bg-secondary-400`}
                disabled={true}
        >
            {text}
        </button>
    )
}

export function SelectedTagButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} bg-secondary-400 hover:bg-muted`}
                onClick={onClick ? () => onClick() : undefined}
        >
            {text}
        </button>
    )
}

export function DeselectedTagButton({type, text, onClick}: ButtonProps) {
    return (
        <button type={type}
                className={`${clickableButtonClass} ${buttonClass} bg-muted hover:bg-secondary-400`}
                onClick={onClick ? () => onClick() : undefined}
        >
            {text}
        </button>
    )
}
