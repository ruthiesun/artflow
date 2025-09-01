type Props = {
    className?: string;
    content: string;
};

export function H1({className, content}: Props) {
    return (
        <h1 className={`${className} font-display-font text-2xl font-bold text-center text-primary break-normal wrap-anywhere`}>
            {content}
        </h1>
    )
}

export function H3({className, content}: Props) {
    return (
        <h3 className={`${className} font-header-font text-xl font-bold text-secondary break-normal wrap-anywhere`}>
            {content}
        </h3>
    )
}

export function Text({className, content}: Props) {
    return (
        <p className={`${className} text-text-font text-foreground break-normal wrap-anywhere`}>
            {content}
        </p>
    )
}

export function TimestampText({className, content}: Props) {
    return (
        <p className={`${className} text-text-font text-muted break-normal wrap-anywhere`}>
            {content}
        </p>
    )
}

export function ErrorText({className, content}: Props) {
    return (
        <p className={`${className} text-text-font text-warning break-normal wrap-anywhere`}>
            {content}
        </p>
    )
}
