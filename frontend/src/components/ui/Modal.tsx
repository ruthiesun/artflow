import type {ReactNode} from "react";

type ModalProps = {
    content: ReactNode;
    onClose: () => void;
};

export function SmallModal({ content, onClose }: ModalProps) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* Background overlay */}
            <div
                className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm"
                onClick={onClose}
            />

            {/* Modal content */}
            <div
                className="relative z-10 bg-white rounded-2xl shadow-xl p-6 max-w-md w-full"
                onClick={(e) => e.stopPropagation()} // prevent closing when clicking inside
            >
                {content}
            </div>
        </div>
    )
}

export function LargeModal({ content, onClose }: ModalProps) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center" onClick={onClose}>
            <div className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm" />
            <div className="relative z-10 w-full h-full">
                {content}
            </div>
        </div>
    )
}

