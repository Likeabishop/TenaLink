import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuthStore } from "@/hooks/auth-store";
import useDocumentTitle from "@/hooks/use-document-title";
import { Role, UserStatus } from "@/utils/types";
import { LockIcon, UserIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const getPasswordStrength = (pwd: string) => {
        let strength = 0;
        if (pwd.length > 5) strength += 1;
        if (pwd.length > 8) strength += 1;
        if (/[A-Z]/.test(pwd)) strength += 1;
        if (/[0-9]/.test(pwd)) strength += 1;
        if (/[^A-Za-z0-9]/.test(pwd)) strength += 1;
        return (strength / 5) * 100;
    };

const Register = () => {
  useDocumentTitle('Register');

  const navigate = useNavigate();
  const { registerClientAdmin, clearError } = useAuthStore();
    
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formData, setFormData] = useState({
      // Basic user fields
      firstname: "", // Java uses 'name' instead of 'firstname'
      lastname: "",
      email: "",
      password: "",
      confirmPassword: "",
      identificationNumber: "",
      
      // Optional fields
      middleNames: "",
      companyName: "",
      
      // Terms acceptance
      acceptTerms: false,
  });

  const strengthPercent = getPasswordStrength(formData.password);

  const handleChange = (
      e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
      const { name, value, type } = e.target;
      
      if (type === 'checkbox') {
          const checkboxTarget = e.target as HTMLInputElement;
          setFormData((prev) => ({ ...prev, [name]: checkboxTarget.checked }));
      } else {
          setFormData((prev) => ({ ...prev, [name]: value }));
      }
      
      // Clear error when user starts typing
      if (errors[name]) {
          setErrors(prev => ({ ...prev, [name]: "" }));
      }
  };

  const validateForm = () => {
      const newErrors: Record<string, string> = {};

      // Required fields
      if (!formData.firstname.trim()) newErrors.firstname = "Name is required";
      if (!formData.lastname.trim()) newErrors.lastname = "Surname is required";
      if (!formData.email.trim()) newErrors.email = "Email is required";
      else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = "Email is invalid";
      
      if (!formData.password) newErrors.password = "Password is required";
      else if (formData.password.length < 6) newErrors.password = "Password must be at least 6 characters";
      
      if (formData.password !== formData.confirmPassword) {
          newErrors.confirmPassword = "Passwords do not match";
      }
      
      if (!formData.identificationNumber.trim()) {
          newErrors.identificationNumber = "Identification number is required";
      }
      
      if (!formData.acceptTerms) {
          newErrors.acceptTerms = "You must accept the terms and conditions";
      }

      setErrors(newErrors);
      return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      
      if (!validateForm()) {
          return;
      }

      // Prepare user data according to Java User entity
      const userPayload = {
          firstname: formData.firstname,
          lastname: formData.lastname,
          middleNames: formData.middleNames || undefined,
          email: formData.email,
          password: formData.password,
          identificationNumber: formData.identificationNumber,
          createdBy: formData.middleNames ? formData.firstname + ' ' + formData.middleNames + formData.lastname : formData.firstname + ' ' + formData.lastname,
      };

      console.log("Submitting registration payload:", userPayload);
      
      try {
          await registerClientAdmin(userPayload);
          
          // Show success message and redirect
          alert(`Registration successful! Please check your email (${formData.email}) to verify your account.`);
          
          // Redirect to login page
          navigate("/");
          
      } catch (error: any) {
          // Error is already set by auth store
          console.error("Registration failed:", error);
      }
  };

  useEffect(() => {
      clearError();
  }, []);

  return (
      <div className="bg-transparent backdrop-blur-sm rounded-xl shadow-2xl overflow-hidden grid grid-cols-1 md:grid-cols-2">
        
        {/* LEFT BRAND PANEL */}
        <div className="text-white p-6 flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-2 mb-10">
              <div className="w-8 h-8 bg-white rounded-md" />
              <span className="font-semibold text-lg text-sky-400">Tenalink</span>
            </div>

            <h1 className="text-4xl font-bold leading-tight">
              Join Our Community,
            </h1>
            
            <h2 className="text-3xl mt-2 font-semibold">
              Create Account
            </h2>
          
            <p className="text-2xl">
              Manage your flats efficiently and modernly!
            </p>
          </div>

          <p className="text-sm opacity-90 max-w-xs">
            Over 5,000 professionals already trust our platform
          </p>
        </div>

        {/* RIGHT FORM PANEL */}
        <div className="bg-gradient-to-r from-sky-400 to-white p-15.5">

          {errors.submit && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg">
            {errors.submit}
          </div>
        )}

          <form onSubmit={handleSubmit} className="space-y-6">
          {/* PERSONAL DATA */}
          <div>
            <p className="text-xs font-semibold text-gray-600 mb-3 uppercase tracking-wide">
              PERSONAL INFORMATION
            </p>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div className="space-y-2">
                <Label htmlFor="firstname">First Name</Label>
                <div className="relative">
                  <Input
                    id="firstname"
                    name="firstname"
                    type="text"
                    placeholder="John"
                    value={formData.firstname}
                    onChange={handleChange}
                  />
                </div>
                {errors.firstname && <p className="text-red-500 text-sm">{errors.firstname}</p>}
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="lastname">Last Name</Label>
                <div className="relative">
                  <Input
                    id="lastname"
                    name="lastname"
                    type="text"
                    placeholder="Doe"
                    value={formData.lastname}
                    onChange={handleChange}
                  />
                </div>
                {errors.lastname && <p className="text-red-500 text-sm">{errors.lastname}</p>}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div className="space-y-2">
                <Label htmlFor="identificationNumber">Identification Number</Label>
                <div className="relative">
                  <Input
                    id="identificationNumber"
                    name="identificationNumber"
                    type="text"
                    placeholder="0000123456789"
                    value={formData.identificationNumber}
                    onChange={handleChange}
                  />
                </div>
                {errors.identificationNumber && <p className="text-red-500 text-sm">{errors.identificationNumber}</p>}
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <div className="relative">
                  <UserIcon className="absolute left-3 top-3 h-5 w-5 text-blue-500" />
                  <Input
                    id="email"
                    name="email"
                    type="email"
                    placeholder="admin@example.com"
                    className="pl-10"
                    value={formData.email}
                    onChange={handleChange}
                  />
                </div>
                {errors.email && <p className="text-red-500 text-sm">{errors.email}</p>}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="password">Password</Label>
                  <div className="relative">
                    <LockIcon className="absolute left-3 top-3 h-5 w-5 text-blue-500" />
                    <Input
                      id="password"
                      name="password"
                      type="password"
                      placeholder="••••••••"
                      className="pl-10"
                      value={formData.password}
                      onChange={handleChange}
                    />
                  </div>
                  {errors.password && <p className="text-red-500 text-sm">{errors.password}</p>}
                </div>

                <div>
                  <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                    <div 
                      className="h-full transition-all duration-300"
                      style={{
                        width: `${strengthPercent}%`,
                        backgroundColor: 
                          strengthPercent < 40 ? '#ef4444' : // red-500
                          strengthPercent < 80 ? '#eab308' : // yellow-500
                          '#22c55e' // green-500
                      }}
                    />
                  </div>
                  
                  <p className={
                    `text-sm mt-1 font-semibold ${
                      strengthPercent < 40
                        ? "text-red-500"
                        : strengthPercent < 80
                        ? "text-yellow-500"
                        : "text-green-500"
                    }`
                  }>
                    {strengthPercent < 40 ? "Weak" : strengthPercent < 80 ? "Moderate" : "Strong"}
                  </p>
                </div>
              </div>
              
              
              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Confirm Password</Label>
                <div className="relative">
                  <LockIcon className="absolute left-3 top-3 h-5 w-5 text-blue-500" />
                  <Input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    placeholder="••••••••"
                    className="pl-10"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                  />
                </div>
                {errors.confirmPassword && <p className="text-red-500 text-sm">{errors.confirmPassword}</p>}
              </div>

            </div>
          </div>

          {/* ROLE AND COMPANY */}
          <div>
            <p className="text-xs font-semibold text-gray-600 mb-3 uppercase tracking-wide">
              Company Information
            </p>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              
              <div className="space-y-2">
                <Label htmlFor="companyName">Company Name</Label>
                <div className="relative">
                  <Input
                    id="companyName"
                    name="companyName"
                    type="text"
                    placeholder="Monsters Inc"
                    value={formData.companyName}
                    onChange={handleChange}
                  />
                </div>
                {errors.companyName && <p className="text-red-500 text-sm">{errors.companyName}</p>}
              </div>
            </div>
          </div>

          {/* Terms */}
          <div className="space-y-2">
            <div className="flex items-start gap-3">
              <input
                type="checkbox"
                name="acceptTerms"
                id="acceptTerms"
                checked={formData.acceptTerms}
                onChange={handleChange}
                className={`mt-1 h-5 w-5 text-sky-600 focus:ring-sky-500 border-gray-300 rounded ${
                  errors.acceptTerms ? 'border-red-500' : ''
                }`}
              />
              <div>
                <label htmlFor="acceptTerms" className="text-sm text-gray-600">
                  I agree to the <a href="#" className="text-sky-600 hover:text-sky-700 underline">Terms of Use</a> and <a href="#" className="text-sky-600 hover:text-sky-700 underline">Privacy Policy</a> *
                </label>
                {errors.acceptTerms && (
                  <p className="text-red-500 text-xs mt-1">{errors.acceptTerms}</p>
                )}
              </div>
            </div>
          </div>

          <button
            type="submit"
            className="w-full bg-gradient-to-r from-sky-500 to-sky-600 hover:from-sky-600 hover:to-sky-700 transition-all duration-300 text-white font-semibold py-3.5 rounded-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:transform-none"
            disabled={Object.keys(errors).length > 0}
          >
            Create Account
          </button>
          
          <p className="text-center text-sm text-gray-600">
            Already have an account?{" "}
            <Link to="/" className="text-sky-600 hover:text-sky-700 font-semibold underline">
              Login here
            </Link>
          </p>
        </form>
        </div>
      </div>
  );
};

export default Register;
