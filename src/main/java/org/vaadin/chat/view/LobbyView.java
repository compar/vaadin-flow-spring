package org.vaadin.chat.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.vaadin.chat.service.Channel;
import org.vaadin.chat.service.ChatService;


@Route(value="", layout = MainLayout.class)
@PageTitle("Lobby")
public class LobbyView extends VerticalLayout {
    private ChatService chatService;
    private VirtualList<Channel> channels;
    private TextField channelNameField;
    private Button addChannelButton;

    public LobbyView(ChatService chatService){
        this.chatService=chatService;
        setSizeFull();

        channels = new VirtualList<>();
        add(channels);
        expand(channels); // 指示 VerticalLayout扩展通道列表的大小以使用所有可用空间
        channels.setRenderer(new ComponentRenderer<>(this::createChannelComponent)); //渲染

        channelNameField = new TextField();
        channelNameField.setPlaceholder("New channel name");  //当文本字段为空时，用户可以在文本字段内看到占位符文本。

        addChannelButton = new Button("Add channel",event -> addChannel());
        addChannelButton.setDisableOnClick(true); //为了防止用户多次触发按钮操作，按钮会在第一次单击后自行禁用。


        var toolbar = new HorizontalLayout(channelNameField,addChannelButton); // HorizontalLayout 是 Vaadin 的内置布局之一，它水平相邻地显示组件。
        toolbar.setWidthFull();
        toolbar.expand(channelNameField);
        add(toolbar);
    }

    private  void refreshChannels(){
        channels.setItems(chatService.channels());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        refreshChannels();
    }

    private  void addChannel(){
        try{
            String newName = channelNameField.getValue(); //空字符，不会是null
            if(!newName.isBlank()){
                chatService.createChannel(newName);
                channelNameField.clear(); //清空
                refreshChannels();  //刷新整个列表
            }
        }finally {
            addChannelButton.setEnabled(true); //开放按钮
        }
    }

    private Component createChannelComponent(Channel channel){
        return new RouterLink(channel.name(),ChannelView.class,channel.id());  //（链接名，导航目标，参数）
    }
}
