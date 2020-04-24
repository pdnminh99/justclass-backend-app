/*
 API01: create classrooms

 [POST] localhost:8080/v1/classroom/{userId}
*/

const api = `localhost:8080/v1/classroom/677e93c8-df80-4029-9d45-ae2432a5bd09`;

// CASE 01: User not found.

// CASE 02: Success.
const classroom01 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "room": 202,
    "theme": 1
}

const classroom06 = {
    "title": "Data Structure & Algorithms",
    "theme": 1
}

const classroom07 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "studentsCount": 10,
    "room": 202,
    "theme": 1
}

const classroom08 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "studentsCount": "1",
    "room": 202,
    "theme": 1
}

const classroom09 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "studentsCount": "1",
    "collaboratorsCount": 1,
    "room": 202,
    "theme": 1
}

const classroom10 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "studentsCount": "1",
    "collaboratorsCount": "1",
    "room": 202,
    "theme": 1
}

const classroom11 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "studentsCount": "1",
    "collaboratorsCount": "1",
    "lastAccess": "blank",
    "lastEdit": "blank",
    "room": 202,
    "theme": 1
}

const classroom11 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "studentsCount": "1",
    "collaboratorsCount": "1",
    "lastAccess": "blank",
    "lastEdit": "blank",
    "role": "123",
    "room": 202,
    "theme": 1
}

// CASE 03
const classroom02 = {
    "title": "",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "room": 202,
    "theme": 1
}

const classroom03 = {
    "title": null,
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "room": 202,
    "theme": 1
}

const classroom04 = {
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "room": 202,
    "theme": 1
}

const classroom05 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "room": 202,
    "theme": null
}

const classroom05 = {
    "title": "Data Structure & Algorithms",
    "description": "No description",
    "section": "101",
    "subject": "CS50",
    "room": 202
}