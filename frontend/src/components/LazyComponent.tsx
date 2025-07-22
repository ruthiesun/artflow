import React, { useEffect, useRef, useState, ReactNode } from 'react';

type LazyComponentProps = {
  children: ReactNode;
  placeholder?: ReactNode;
  rootMargin?: string;
  threshold?: number;
  className?: string; // any extra classes
};

export const LazyComponent: React.FC<LazyComponentProps> = ({
  children,
  placeholder = null,
  rootMargin = '0px',
  threshold = 0.1,
  className = '',
}) => {
  const ref = useRef<HTMLDivElement | null>(null);
  const [isVisible, setIsVisible] = useState(false);
  const [hasAnimatedIn, setHasAnimatedIn] = useState(false);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !isVisible) {
          setIsVisible(true);
          setTimeout(() => setHasAnimatedIn(true), 50); // slight delay to trigger animation
          if (ref.current) observer.unobserve(ref.current);
        }
      },
      { rootMargin, threshold }
    );

    if (ref.current) {
      observer.observe(ref.current);
    }

    return () => {
      if (ref.current) observer.unobserve(ref.current);
    };
  }, [isVisible, rootMargin, threshold]);

  return (
    <div
      ref={ref}
      className={`
        opacity-0 transition-opacity duration-700
        ${isVisible && hasAnimatedIn ? 'opacity-100' : ''}
        ${className}
      `}
    >
      {isVisible ? children : placeholder}
    </div>
  );
};

