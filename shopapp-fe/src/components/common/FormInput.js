import React from 'react';

const FormInput = ({
    type = 'text',
    placeholder,
    value,
    onChange,
    required = false,
    className = '',
    ...props
}) => {
    return (
        <input
            type={type}
            className={`form-control ${className}`}
            placeholder={placeholder}
            required={required}
            value={value}
            onChange={onChange}
            {...props}
        />
    );
};

export default FormInput; 