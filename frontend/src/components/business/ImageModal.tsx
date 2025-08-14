import {useEffect, useState} from "react";
import type {ProjectImageElem, ProjectImagePrePersist} from "../../types/image";
import {SmallModal} from "../ui/Modal.tsx"
import {PrimaryButton} from "../ui/Button.tsx"
import {Input, TextAreaInput, DateInput} from "../ui/Input.tsx"

type EditImageModalProps = {
    editingImage: ProjectImageElem,
    setImages: ((prev: ProjectImageElem[]) => void),
    images: ProjectImageElem[]
    onClose: (() => void)
}

export function EditImageModal({ editingImage, setImages, images, onClose }: EditImageModalProps) {
    const [url, setUrl] = useState<string>(editingImage.url);
    const [date, setDate] = useState<string>(editingImage.dateTime ? editingImage.dateTime.split("T")[0] : "");
    const [caption, setCaption] = useState<string>(editingImage.caption);
    const [error, setError] = useState<string | null>(null);

    const updateImage = (() => {
        if (!url) {
            setError("URL must be nonempty")
            return
        }
        
        const newDateTime = date === "" ? null : new Date(date + "T00:00:00");

        const updatedImages = images.map((img) =>
            img.position === editingImage.position
                ? { ...img, url: url, dateTime: newDateTime, caption: caption }
                : img
        );
        setImages(updatedImages);
        onClose();
    })

    return (
        <SmallModal content={
            (
                <div>
                    {error && <p>{error}</p>}
                    <Input label="Url" type="text" value={url} setValue={setUrl} />
                    <TextAreaInput label="Caption" type="text" value={caption} setValue={setCaption} />
                    <DateInput label="Date" value={date} setValue={setDate} />
                    <PrimaryButton type='button' text="Update" disabled={url.trim() === ""} onClick={updateImage} />
                </div>
            )
        } onClose={onClose}/>
    )
}

type AddImageModalProps = {
    projectName: string,
    setImages: ((prev: ProjectImageElem[]) => void),
    images: ProjectImageElem[]
    onClose: (() => void)
}

export function AddImageModal({projectName, setImages, images, onClose}: AddImageModalProps) {
    const [url, setUrl] = useState<string>("");
    const [date, setDate] = useState<string>("");
    const [caption, setCaption] = useState<string>("");
    const [error, setError] = useState<string | null>(null);

    const updateImages = (() => {
        if (!url) {
            setError("URL must be nonempty")
            return
        }

        const dateTime = date === "" ? null : new Date(date + "T00:00:00");

        const newImage: ProjectImagePrePersist = {position: images.length, caption: caption, dateTime: dateTime, url: url, projectName: projectName}
        setImages([...images, newImage]);
        onClose()
    })

    return (
        <SmallModal content={(
            <div>
                {error && <p>{error}</p>}
                <Input label="Url" type="text" value={url} setValue={setUrl} />
                <TextAreaInput label="Caption" type="text" value={caption} setValue={setCaption} />
                <DateInput label="Date" value={date} setValue={setDate} />
                <PrimaryButton type='button' text="Create" disabled={url.trim() === ""} onClick={updateImages} />
            </div>
        )} onClose={onClose}/>
    )
}

