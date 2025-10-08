# Custom Fonts for Invoice Templates

This directory contains custom TTF fonts used in invoice templates. The application will gracefully fall back to system fonts if these files are not present.

## Required Fonts

### For Modern Template (Roboto)
- `Roboto-Regular.ttf`
- `Roboto-Bold.ttf`
- `Roboto-Italic.ttf`

### For Minimal Template (Open Sans)
- `OpenSans-Regular.ttf`
- `OpenSans-Bold.ttf`
- `OpenSans-Italic.ttf`

### For Modern Template Accents (Montserrat)
- `Montserrat-Regular.ttf`
- `Montserrat-Bold.ttf`

## Where to Get Fonts

### Roboto
- Download from: https://fonts.google.com/specimen/Roboto
- License: Apache License 2.0

### Open Sans
- Download from: https://fonts.google.com/specimen/Open+Sans
- License: Apache License 2.0

### Montserrat
- Download from: https://fonts.google.com/specimen/Montserrat
- License: SIL Open Font License

## Installation

1. Download the font files from the URLs above
2. Extract the TTF files
3. Copy the required TTF files to this directory (`src/main/resources/static/fonts/`)
4. Rebuild the application if already running

## Font Loading

The application automatically registers these fonts at startup using the `CustomFontResolver` class. If a font file is missing, the application will:

1. Log a warning message
2. Fall back to system fonts (Arial, Helvetica, etc.)
3. Continue generating PDFs without errors

## Testing Fonts

After adding font files:

1. Restart the application
2. Generate an invoice with each template (default, modern, minimal)
3. Open the generated PDF to verify fonts are embedded correctly
4. Check application logs for any font-related warnings
