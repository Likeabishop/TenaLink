import { FaUserCircle } from "react-icons/fa";

interface ProfileImageProps {
    profilePicUrl?: string;
    profilePicture?: string;
}

const ProfileImage: React.FC<ProfileImageProps> = ({ profilePicUrl, profilePicture }) => (
    profilePicUrl ? (
        <img
            src={profilePicUrl}
            alt="User"
            className="rounded-full w-15 h-15"
        /> 
    ) : profilePicture ? (
        <img
            src={`data:image/jpeg;base64,${profilePicture}`}
            alt="User"
            className="rounded-full w-15 h-15"
        /> 
    ) : (
        <FaUserCircle className="w-15 h-15" />
    )
)

export default ProfileImage;