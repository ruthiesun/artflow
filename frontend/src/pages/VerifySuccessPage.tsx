import {useNavigate, useSearchParams} from "react-router-dom";
import {useEffect, useState} from "react";
import { verify } from "../api/auth.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {Background, BackgroundBorder, EdgePadding} from "../components/ui/Background.tsx";
import {Text} from "../components/ui/Text.tsx";


export function VerifySuccessPage() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const nav = useNavigate();
    const [verificationStatusMessage, setVerificationStatusMessage] = useState<string>("Verifying...");

    useEffect(() => {
        if (token) {
            verify(token)
                .then(() => {
                    setVerificationStatusMessage("Verified account.");
                })
                .catch(err => {
                    console.log("error")
                    // navToErrorPage(nav, err);
                })
        }
  }, [token]);

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <Text content={`${verificationStatusMessage}`} />
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    );
}