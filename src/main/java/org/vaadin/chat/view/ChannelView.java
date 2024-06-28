package org.vaadin.chat.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.chat.service.Channel;
import org.vaadin.chat.service.ChatService;
import org.vaadin.chat.service.Message;
import org.vaadin.chat.util.LimitedSortedAppendOnlyList;
import reactor.core.Disposable;

import java.util.*;

@Route(value = "channel",layout = MainLayout.class)
public class ChannelView extends VerticalLayout  //垂直布局
    implements HasUrlParameter<String>, HasDynamicTitle { // 动态标题需实现 HasDynamicTitle 接口
    private static final Logger log = LoggerFactory.getLogger(ChannelView.class);   //url参数是String

    private final MessageList messageList;
    private final ChatService chatService;
    private String channelId;
    private String channelName;
//    private final List<Message> receivedMessages = new ArrayList<>();
    private static final int HISTORY_SIZE = 20;
    private final LimitedSortedAppendOnlyList<Message> receivedMessages;

    public  ChannelView(ChatService chatService){
        this.chatService=chatService;
        receivedMessages = new LimitedSortedAppendOnlyList<>(
                HISTORY_SIZE,  // 该列表仅包含二十条消息。
                Comparator.comparing(Message::sequenceNumber) //消息将按其序列号排序，序列号是服务器接收消息的编号。
        );
        setSizeFull();  // 长宽都是100%
        messageList = new MessageList();
        add(messageList);

        MessageInput messageInput = new MessageInput(event -> sendMessage(event.getValue())); //添加事件
        messageInput.setWidthFull();  //宽度100%
        add(messageInput);



    }
    @Override
    public void setParameter(BeforeEvent event, String channelId) {
        chatService.channel(channelId).ifPresentOrElse(
                channel -> this.channelName = channel.name(),//此行表示如果通道 ID 有效，则将名称存储在 channelName 字段中。
                () ->  event.forwardTo(LobbyView.class) //这表示如果无效，请导航回大厅视图。
        );
        this.channelId=channelId;

    }

    private  void  sendMessage(String message){
        if(!message.isBlank()){
            chatService.postMessage(channelId,message);
        }
    }


    private MessageListItem createMessageListItem(Message message) {
        var item = new MessageListItem(
                message.message(),
                message.timestamp(),
                message.author()
        );
        return item;
    }
    private void receiveMessages(List<Message> incoming) {  //服务提供批量消息
        getUI().ifPresent(ui -> ui.access(() -> {  //每当从 HTTP 请求线程以外的线程更新 Vaadin 用户界面时，
            // 都必须使用 UI.access() 。该方法将确保会话在更新期间正确锁定，并在完成后将更改推送到浏览器。
            receivedMessages.addAll(incoming);
            messageList.setItems(receivedMessages.stream()
                    .map(this::createMessageListItem)
                    .toList());  //重建
        }));
    }

    private Disposable subscribe() {
        var subscription = chatService
                .liveMessages(channelId)
                .subscribe(this::receiveMessages); //每当发出 Flux 一批新的消息时，都会调用该 receiveMessages() 方法。
        var lastSeenMessageId = receivedMessages.getLast()  //客户端收到的最新消息是 receivedMessages 列表中的最后一条消息。
                .map(Message::messageId).orElse(null); //如果列表为空，则应将 null 作为最新消息传递。
        receiveMessages(chatService.messageHistory(
                channelId,  //通道ID已在私有字段中可用。
                HISTORY_SIZE,  //最多可发送20条消息。
                lastSeenMessageId
        ));
        return subscription; //您需要对订阅的引用，以便在不再需要订阅时取消订阅。
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        var subscription = subscribe();  //当视图附加到 UI 并变得可见时，这表示订阅后端服务。
        addDetachListener(event -> {
            subscription.dispose();
            log.info("解绑订阅"+channelId);
        });  //每当视图与 UI 分离时，此行都会显示取消订阅。
    }

    @Override
    public String getPageTitle() {
        return channelName;
    }
}
