export interface User {
  id: number;
  fullName: string;
  phoneNumber?: string;
  address: string;
  dateOfBirth: Date; 
  avatar?: string;
  facebookAccountId?: string;
  googleAccountId?: string;
  role: string;
  createdAt?: Date;
  updatedAt?: Date;
}