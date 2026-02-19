
// import { useEffect, useState } from "react";

// const useFacilityAdminsFetch = () => {
//     const [facilityAdmins, setFacilityAdmins] = useState<FacilityAdminResponseDTO[]>(null);

//     useEffect(() => {
//         const fetchData = async () => {
//             const admins = await getAllFacilityAdmins();
//             setFacilityAdmins(admins)
//         }

//         fetchData();
//     }, []);

//     return facilityAdmins;
// }

// const useFacilityAdminByEmailFetch = (email: string) => {
//     const [facilityAdmin, setFacilityAdmin] = useState<FacilityAdminResponseDTO>(null);

//     useEffect(() => {
//         const fetchData = async () => {
//             const admin = await getFacilityAdminByEmail(email);
//             setFacilityAdmin(admin)
//         }

//         fetchData();
//     }, [email]);

//     return facilityAdmin;
// }

// const useFacilityAdminByPracticeFetch = (practiceNumber: string) => {
//     const [facilityAdmin, setFacilityAdmin] = useState<FacilityAdminResponseDTO>(null);

//     useEffect(() => {
//         const fetchData = async () => {
//             const admin = await getFacilityAdminByPracticeNumber(practiceNumber);
//             setFacilityAdmin(admin)
//         }

//         fetchData();
//     }, [practiceNumber]);

//     return facilityAdmin;
// }

// const useFacilityAdminByMedicalFacilityFetch = (medicalFacilityId: string) => {
//     const [facilityAdmins, setFacilityAdmins] = useState<FacilityAdminResponseDTO[]>(null);

//     useEffect(() => {
//         const fetchData = async () => {
//             const admins = await getFacilityAdminsByMedicalFacility(medicalFacilityId);
//             setFacilityAdmins(admins)
//         }

//         fetchData();
//     }, [medicalFacilityId]);

//     return facilityAdmins;
// }

// const useActiveFacilityAdminFetch = () => {
//     const [facilityAdmins, setFacilityAdmins] = useState<FacilityAdminResponseDTO[]>(null);

//     useEffect(() => {
//         const fetchData = async () => {
//             const admins = await getActiveFacilityAdmins();
//             setFacilityAdmins(admins)
//         }

//         fetchData();
//     }, []);

//     return facilityAdmins;
// }

// export {
//     useFacilityAdminsFetch,
//     useFacilityAdminByEmailFetch,
//     useFacilityAdminByPracticeFetch,
//     useFacilityAdminByMedicalFacilityFetch,
//     useActiveFacilityAdminFetch
// }