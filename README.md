# B.S. in Software Engineering - Capstone Project: Frontier Companion

An Android application for the Frontier Culture Museum of Virginia that provides visitors with semantic search capabilities across museum content and interactive exhibit exploration.

## Overview

Frontier Companion enhances the museum visitor experience by enabling natural language search across historical narratives from various exhibits and providing interactive map-based navigation. The application uses AI-powered semantic search to help visitors discover relevant content based on their interests.

## Architecture

The project follows a three-tier architecture:

### Database Layer
- PostgreSQL with pgvector extension for vector similarity search
- Stores exhibit narratives and their OpenAI embeddings

### Backend Layer
- Spring Boot REST API with OpenAI integration
- Dual authentication system for app and admin access
- Comprehensive logging and monitoring

### Frontend Layer
- Android native application
- Firebase Analytics integration
- Interactive map functionality

## Features

- **Semantic Search**: Natural language queries across museum content using AI-powered vector similarity search
- **Exhibit Coverage**: German Farm, Irish Farm, English Farm, and West African Exhibit
- **Interactive Map**: Visual exhibit navigation with location-based features
- **Analytics**: User interaction and search query tracking
- **Admin Tools**: Content management via REST API

## Technology Stack

**Backend**: Spring Boot, PostgreSQL with pgvector, OpenAI API, Docker  
**Frontend**: Android, Firebase Analytics, Google Maps  
**Infrastructure**: Render (hosting), Firebase (distribution)

## Project Structure
```
frontier-companion/
├── frontierCompanion-backend/     # Spring Boot backend application
│   └── README.md                  # Backend API documentation
├── frontierCompanion-android/     # Android application
│   ├── README.md                  # App user guide
│   └── TECHNICAL_GUIDE.md         # Android technical documentation
└── README.md                      # This file
```

## Documentation

For detailed setup instructions, configuration, and usage:

- **Backend API Documentation**: See `frontierCompanion-backend/README.md`
- **Android User Guide**: See `frontierCompanion-android/README.md`
- **Android Technical Guide**: See `frontierCompanion-android/TECHNICAL_GUIDE.md`

## Quick Start

1. **Backend**: Navigate to `frontierCompanion-backend/` and follow the setup instructions in the API manual
2. **Android**: Navigate to `frontierCompanion-android/` and follow the technical guide for development setup
3. **Deployment**: Both directories contain deployment documentation for production environments

## Security

- API keys and sensitive configuration should never be committed to version control
- Use environment variables for all secrets
- Separate development and production configurations
- HTTPS required for production deployments

## Contact

For questions or support regarding the Frontier Companion project, please contact joseph@josephbassett.dev.

## Acknowledgments

- Frontier Culture Museum of Virginia
- OpenAI for embedding API
- Firebase for analytics and distribution
- Render for hosting infrastructure