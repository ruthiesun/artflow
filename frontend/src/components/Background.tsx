type Props = {
    className: string;
    content: ReactNode;
};

export function Background({className, content}: Props) {
    return (
        <div className={`min-h-screen flex items-center justify-center bg-bg ${className}`}>
            {content}
        </div>
    )
}

export function BackgroundBorder({className, content}: Props) {
    return (
        <div className={`w-full rounded-2xl shadow-lg p-8 bg-backing ${className}`}>
            {content}
        </div>
    )
}


