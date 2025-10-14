import validatorJson from "../../shared/validation.json";

const usernameField: string = "username";
const passwordField: string = "password";
const projectNameField: string = "projectName";
const projectDescriptionField: string = "projectDescription";
const projectImageUrlField: string = "projectImageUrl";
const projectImageCaptionField: string = "projectImageCaption";
const tagField: string = "tag";

interface ValidationRule {
    regex: string;
    message: string;
    minLength: number;
    maxLength: number;
}

export class Validator {
    private static instance: Validator;
    private settings: Record<string, ValidationRule>;

    private constructor(settings: Record<string, any>) {
        this.settings = settings;
    }

    public static async getInstance(): Promise<Validator> {
        if (!Validator.instance) {
            Validator.instance = new Validator(validatorJson);
        }

        return Validator.instance;
    }

    public getUsernameRegex(): string {
        return this.settings[usernameField].regex;
    }

    public getUsernameMessage(): string {
        return this.settings[usernameField].message;
    }

    public getPasswordRegex(): string {
        return this.settings[passwordField].regex;
    }

    public getPasswordMessage(): string {
        return this.settings[passwordField].message;
    }

    public getProjectNameRegex(): string {
        return this.settings[projectNameField].regex;
    }

    public getProjectNameMessage(): string {
        return this.settings[projectNameField].message;
    }

    public getProjectDescriptionRegex(): string {
        return this.settings[projectDescriptionField].regex;
    }

    public getProjectDescriptionMessage(): string {
        return this.settings[projectDescriptionField].message;
    }

    public getProjectImageUrlRegex(): string {
        return this.settings[projectImageUrlField].regex;
    }

    public getProjectImageUrlMessage(): string {
        return this.settings[projectImageUrlField].message;
    }

    public getProjectImageCaptionRegex(): string {
        return this.settings[projectImageCaptionField].regex;
    }

    public getProjectImageCaptionMessage(): string {
        return this.settings[projectImageCaptionField].message;
    }

    public getTagRegex(): string {
        return this.settings[tagField].regex;
    }

    public getTagMessage(): string {
        return this.settings[tagField].message;
    }
}

