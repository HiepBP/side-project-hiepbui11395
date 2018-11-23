package hpbui.gamerportal.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hpbui.gamerportal.entity.Account;
import hpbui.gamerportal.entity.Comment;
import hpbui.gamerportal.service.AccountService;
import hpbui.gamerportal.service.CommentService;
import hpbui.gamerportal.utils.JQueryDataTable;
import hpbui.gamerportal.utils.Utils;
import hpbui.gamerportal.viewmodel.CommentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentRestController {
    @Autowired
    CommentService commentService;
    @Autowired
    AccountService accountService;

    @PostMapping(value = "/api/comments/add")
    @ResponseStatus(value = HttpStatus.OK)
    public void addComment(CommentViewModel model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        Account accountFrom = accountService.findAccountByUsername(userDetails.getUsername());
        commentService.addComment(accountFrom.getId(), model.getToUserId(),
                model.getText(), model.getScore(), model.getParentId());
    }

    @GetMapping(value = "/api/comments/{userId}")
    public String getComments(JQueryDataTable dataTable, @PathVariable long userId) {
        String sEcho = dataTable.getsEcho();
        String query = dataTable.getsSearch();
        long iTotalRecords; // total number of records (unfiltered)
        long iTotalDisplayRecords;//value will be set when code filters companies by keyword
        Page<Comment> commentList;
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "commentTime"));
        commentList = commentService.findCommentsByToUserId(userId, PageRequest.of(
                (dataTable.getiDisplayStart() / dataTable.getiDisplayLength()),
                dataTable.getiDisplayLength(), sort));

        iTotalRecords = commentList.getTotalElements();
        iTotalDisplayRecords = commentList.getTotalElements();
        JsonObject dataTableResponse = new JsonObject();
        dataTableResponse.addProperty("sEcho", sEcho);
        dataTableResponse.addProperty("iTotalRecords", iTotalRecords);
        dataTableResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
        JsonArray data = new JsonArray();
        for (Comment comment : commentList) {
            JsonArray row = new JsonArray();
            row.add(new JsonPrimitive(comment.getFromUser().getUsername()));
            row.add(new JsonPrimitive(comment.getText()));
            row.add(new JsonPrimitive(comment.getScore()));
            row.add(new JsonPrimitive(Utils.convertTimeStamp(comment.getCommentTime())));
            data.add(row);
        }
        dataTableResponse.add("aaData", data);
        return dataTableResponse.toString();
    }
}
