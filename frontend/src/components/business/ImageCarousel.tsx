import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import type {ProjectImage} from "../../types/image";
import {getImagesForProject} from "../../api/images.ts";
import {LargeModal} from "../ui/Modal.tsx";
import { navToErrorPage } from "../../pages/ErrorPage.tsx";
import {H1, H3, Text, TimestampText} from "../ui/Text.tsx";
import { EdgePadding } from "../ui/Background.tsx";

type ImageDisplayProps = {
    image: ProjectImage;
    className?: string;
    onClick?: () => void;
}

function ImageDisplaySmall({image, className}: ImageDisplayProps) {
    return (
        <img
        src={image.url}
        alt={`image in position: ${image.position}`}
        className={`${className} h-full rounded-sm inline-block object-contain shadow-sm`}
    />)
}

type ImageDetailsProps = {
    image: ProjectImage;
    onClose: () => void
}

function ImageDetails({image, onClose}: ImageDetailsProps) {
    return (
        <LargeModal content={
            <img src={image.url} alt="large image" className="object-contain w-full h-full" />
        } onClose={onClose} />
    )
}

type ImageCarouselProps = {
    projectName: string;
};

export function ImageCarousel({ projectName }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])
    const [selectedImage, setSelectedImage] = useState<ProjectImage>(null)
    const [showImageDetailsModal, setShowImageDetailsModal] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    useEffect(() => {
        getImagesForProject(projectName)
            .then((projectImages) =>
                setImages(projectImages)
            )
            .catch((err) => {
                navToErrorPage(nav, err);
            })
        }, []);

    const prepareToShowImageDetails = (img: ProjectImage) => {
        setSelectedImage(img);
        setShowImageDetailsModal(true);
    }

    // return (
    //     <div className="flex-col flex justify-center items-center lg:items-start">
    //         {images.map((image, index) => (
    //             <div key={image.position} className="w-full pb-1">
    //                 <div className="flex flex-col items-center lg:flex-row lg:items-start py-1 bg-surface">
    //                     <EdgePadding>
    //                         <img
    //                             src={image.url}
    //                             alt={`image in position: ${image.position}`}
    //                             className={`cursor-pointer h-full max-h-[75vh] rounded-lg object-contain shadow-sm lg:max-w-1/2`}
    //                             onClick={() => prepareToShowImageDetails(image)}
    //                         />
    //                         <div className="flex flex-col items-center max-w-full lg:items-start lg:pl-5">
    //                             {image.dateTime == null ? (
    //                                 <TimestampText content="Date unknown" className="max-w-full" />
    //                             ) : (
    //                                 <TimestampText content={`${image.dateTime.split("T")[0]}`} className="max-w-full" />
    //                             )}
    //                             <Text content={image.caption} className="max-w-full" />
    //                         </div>
    //                     </EdgePadding>
    //                 </div>
    //             </div>
    //         ))}
    //         {selectedImage && showImageDetailsModal && <ImageDetails image={selectedImage} onClose={() => setShowImageDetailsModal(false)} />}
    //     </div>
    // )


    return (
        <div className="flex-col flex justify-center items-center lg:items-start">
            {images.map((image, index) => (
                <div key={image.position} className="w-full pb-1 md:pb-2">
                    <EdgePadding className="py-1 bg-surface">
                        <div className="flex flex-col items-center lg:flex-row lg:items-start max-w-full">
                            <img
                                src={image.url}
                                alt={`image in position: ${image.position}`}
                                className={`cursor-pointer h-full max-h-[75vh] rounded-lg object-contain shadow-sm lg:max-w-1/2`}
                                onClick={() => prepareToShowImageDetails(image)}
                            />
                            <div className="flex flex-col items-center max-w-full lg:items-start lg:pl-5">
                                {image.dateTime == null ? (
                                    <TimestampText content="Date unknown" />
                                ) : (
                                    <TimestampText content={`${image.dateTime.split("T")[0]}`} />
                                )}
                                <Text content={image.caption} />
                            </div>
                        </div>
                    </EdgePadding>
                </div>
            ))}
            {selectedImage && showImageDetailsModal && <ImageDetails image={selectedImage} onClose={() => setShowImageDetailsModal(false)} />}
        </div>
    )
}

export function ImageCarouselPreview({ projectName, }: ImageCarouselProps) {
    const [images, setImages] = useState<ProjectImage[]>([])
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    useEffect(() => {
        getImagesForProject(projectName)
            .then((projectImages) =>
                setImages(projectImages)
            )
            .catch((err) => {
                navToErrorPage(nav, err);
            })
    }, []);

    return (
        <div className="overflow-x-auto whitespace-nowrap
        flex-row flex
        space-x-1 sm:space-x-2 md:space-x-3 lg:space-x-4
        p-1 sm:p-2 md:p-3 lg:p-4
        scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100">
            {images.map((image, index) => (
                <div key={index} className="flex-shrink-0
                h-40 sm:h-48 md:h-56 lg:h-64">
                    <ImageDisplaySmall image={image} />
                </div>
            ))}
        </div>
    )
}
