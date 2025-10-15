import { useEffect, useState } from "react";
import type { ProjectImageElem, ProjectImagePrePersist } from "../../types/image";
import { SmallModal } from "../ui/Modal.tsx"
import { PrimaryButton } from "../ui/Button.tsx"
import { Input, TextAreaInput, DateInput } from "../ui/Input.tsx"
import { ErrorText } from "../ui/Text.tsx";
import { Validator } from "../../Validator.ts";
import { navToErrorPage } from "../../pages/ErrorPage.tsx";
import { useNavigate } from "react-router-dom";

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
    const [validator, setValidator] = useState<Validator>();
    const nav = useNavigate();

    useEffect(() => {
        Validator.getInstance()
            .then((res) => {
                setValidator(res);
            })
            .catch((err) => {
                navToErrorPage({ nav, err });
            });
    }, []);

    const updateImage = (() => {
        if (!url) {
            setError("URL must be nonempty");
            return;
        }

        if (validator === undefined) {
            return;
        }

        if (!(new RegExp(validator.getProjectImageUrlRegex()).test(url))) {
            setError(validator.getProjectImageUrlMessage());
            return;
        }

        if (!(new RegExp(validator.getProjectImageCaptionRegex()).test(caption))) {
            setError(validator.getProjectImageCaptionMessage());
            return;
        }

        const newDateTime: string = date === "" ? "" : date + "T00:00:00";

        const updatedImages: ProjectImageElem[] = images.map((img: ProjectImageElem) =>
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
                    <TextAreaInput label="Caption" value={caption} setValue={setCaption} />
                    <DateInput label="Date" value={date} setValue={setDate} />
                    {error && <ErrorText className="mb-4" content={error} />}
                    <PrimaryButton type='button' text="Update" disabled={url.trim() === ""} onClick={updateImage} />
                </div>
            )
        } onClose={onClose} />
    )
}

type AddImageModalProps = {
    projectName: string,
    setImages: ((prev: ProjectImageElem[]) => void),
    images: ProjectImageElem[]
    onClose: (() => void)
}

export function AddImageModal({ projectName, setImages, images, onClose }: AddImageModalProps) {
    const [url, setUrl] = useState<string>("");
    const [date, setDate] = useState<string>("");
    const [caption, setCaption] = useState<string>("");
    const [error, setError] = useState<string | null>(null);

    const updateImages = (() => {
        if (!url) {
            setError("URL must be nonempty")
            return
        }

        const dateTime: string | null = date === "" ? null : (new Date(date + "T00:00:00")).toISOString();

        const newImage: ProjectImagePrePersist = { position: images.length, caption: caption, dateTime: dateTime, url: url, projectName: projectName }
        setImages([...images, newImage]);
        onClose()
    })

    return (
        <SmallModal content={(
            <div>
                {error && <p>{error}</p>}
                <Input label="Url" type="text" value={url} setValue={setUrl} />
                <TextAreaInput label="Caption" value={caption} setValue={setCaption} />
                <DateInput label="Date" value={date} setValue={setDate} />
                <PrimaryButton type='button' text="Create" disabled={url.trim() === ""} onClick={updateImages} />
            </div>
        )} onClose={onClose} />
    )
}

