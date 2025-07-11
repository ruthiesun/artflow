type ProjectVisibilityRadioProps = {
    visibility: "public" | "private";
    setVisibility: ((newValue: "public" | "private") => void)
};

export function ProjectVisibilityRadio({ visibility, setVisibility }: ProjectVisibilityRadioProps) {
    return (
        <div>
            <label>Visibility</label>
            <div>
                <label>
                    <input
                        type="radio"
                        name="visibility"
                        value="public"
                        checked={visibility == "public"}
                        onChange={() => setVisibility("public")}
                    />
                    Public
                </label>
                <label>
                    <input
                        type="radio"
                        name="visibility"
                        value="private"
                        checked={visibility == "private"}
                        onChange={() => setVisibility("private")}
                    />
                    Private
                </label>
            </div>
        </div>
    )
}
