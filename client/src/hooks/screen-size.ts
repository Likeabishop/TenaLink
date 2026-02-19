import { useEffect, useState } from "react"

export const useIsLargeScreen = () => {
    const [isLarge, setIsLarge] = useState<boolean>(false);
    console.log('isLarge: ', isLarge)
    useEffect(() => {
        const mediaQuery = window.matchMedia("(min-width: 1024px)");

        const handleChange = (e: MediaQueryListEvent) => setIsLarge(e.matches);

        setIsLarge(mediaQuery.matches);
        mediaQuery.addEventListener("change", handleChange);

        return () => mediaQuery.removeEventListener("change", handleChange);
    }, []);

    return isLarge;
} 