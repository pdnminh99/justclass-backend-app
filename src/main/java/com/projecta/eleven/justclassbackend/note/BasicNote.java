package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicNote implements MapSerializable {

    private String Id;

    public NoteType type;

    private MinifiedUser author;

    private String content;

    private Timestamp createAt;

    private Integer commentsCount;

    private String classroomId;
    private DocumentReference authorReference;
    private List<String> links;

    private DocumentReference classroomReference;

    public BasicNote(
            String Id,
            MinifiedUser author,
            DocumentReference authorReference,
            String content,
            Timestamp createAt,
            Integer commentsCount,
            String classroomId,
            DocumentReference classroomReference,
            List<String> links,
            NoteType type
    ) {
        this.Id = Id;
        this.author = author;
        this.authorReference = authorReference;
        setContent(content);
        this.createAt = createAt;
        this.commentsCount = commentsCount;
        this.classroomId = classroomId;
        this.classroomReference = classroomReference;
        this.type = type;
        setLinks(links);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public MinifiedUser getAuthor() {
        return author;
    }

    public void setAuthor(MinifiedUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content != null) {
            links.addAll(extractUrls(content));
        }
        this.content = content;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public DocumentReference getClassroomReference() {
        return classroomReference;
    }

    public void setClassroomReference(DocumentReference classroomReference) {
        this.classroomReference = classroomReference;
    }

    public DocumentReference getAuthorReference() {
        return authorReference;
    }

    public void setAuthorReference(DocumentReference authorReference) {
        this.authorReference = authorReference;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        if (links == null) {
            this.links = null;
            return;
        }
        this.links.clear();
        for (String link : links) {
            links.addAll(extractUrls(link));
        }
    }

    public void addLink(String URL) {
        if (URL != null) {
            this.links.addAll(extractUrls(URL));
        }
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    private List<String> extractUrls(String text) {
        List<String> containedUrls = Lists.newArrayList();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        if (getAuthor() != null) {
            ifFieldNotNullThenPutToMap("author", getAuthor().toMap(), map);
        }
        ifFieldNotNullThenPutToMap("authorReference", getAuthorReference(), map);
        ifFieldNotNullThenPutToMap("content", getContent(), map);
        if (getCreateAt() != null) {
            ifFieldNotNullThenPutToMap("createAt", isTimestampInMilliseconds ?
                    getCreateAt().toDate().getTime() :
                    getCreateAt(), map);
        }
        ifFieldNotNullThenPutToMap("commentsCount", getCommentsCount(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        ifFieldNotNullThenPutToMap("links", getLinks(), map);
        if (getType() != null) {
            ifFieldNotNullThenPutToMap("type", getType().toString(), map);
        }
        return map;
    }
}
