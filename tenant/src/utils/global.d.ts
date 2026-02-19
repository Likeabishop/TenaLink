declare module "jspdf" {
    interface jsPDF {
      previousAutoTable?: { finalY: number }; // Declare the property
    }
  }
  