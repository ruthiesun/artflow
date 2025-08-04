import {useState} from "react";
import type {ProjectImageElem, ProjectImagePrePersist} from "../../types/image";
import {SmallModal} from "../ui/Modal.tsx"
import {SubmitButton} from "../ui/Button.tsx"
import {Input, TextAreaInput, DateInput} from "../ui/Input.tsx"

type EditImageModalProps = {
    editingImage: ProjectImageElem,
    setImages: ((prev: ProjectImageElem[]) => void),
    images: ProjectImageElem[]
    onClose: (() => void)
}

export function EditImageModal({ editingImage, setImages, images, onClose }: EditImageModalProps) {
    const [url, setUrl] = useState<string>(editingImage.url);
    const [date, setDate] = useState<Date>(editingImage.dateTime);
    const [caption, setCaption] = useState<string>(editingImage.caption);
    const [error, setError] = useState<string | null>(null);

    const updateImage = (() => {
        if (!url) {
            setError("URL must be nonempty")
            return
        }

        const updatedImages = images.map((img) =>
            img.position === editingImage.position
                ? { ...img, url: url, dateTime: date, caption: caption }
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
                    <DateInput label="Date" type="text" value={date} setValue={setDate} />
                    <SubmitButton type='button' text="Update" disabled={url.trim() === ""} onClick={updateImage} />
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
    const [date, setDate] = useState<Date>(new Date());
    const [caption, setCaption] = useState<string>("");
    const [error, setError] = useState<string | null>(null);

    const updateImages = (() => {
        if (!url) {
            setError("URL must be nonempty")
            return
        }
        const newImage: ProjectImagePrePersist = {position: images.length, caption: caption, dateTime: date, url: url, projectName: projectName}
        setImages([...images, newImage]);
        onClose()
    })

    return (
        <SmallModal content={(
            <div>
                {error && <p>{error}</p>}
                <Input label="Url" type="text" value={url} setValue={setUrl} />
                <TextAreaInput label="Caption" type="text" value={caption} setValue={setCaption} />
                <DateInput label="Date" type="text" value={date} setValue={setDate} />
                <SubmitButton type='button' text="Create" disabled={url.trim() === ""} onClick={updateImages} />
            </div>
        )} onClose={onClose}/>
    )
}

