package org.vaadin.chat.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {
    //Applayout分为三个部分：水平导航栏，称为导航栏；一个可折叠的导航抽屉，称为抽屉；和一个内容区域，在其中呈现实际视图。

    private H2 viewTitle;

    public MainLayout(){
        setPrimarySection(Section.DRAWER); //这使得抽屉填充屏幕的整个高度，将标题（即导航栏）移动到屏幕的一侧。

        addNavbarContent();
        addDrawerContent();
    }

    private void addDrawerContent() {
        var toggle = new DrawerToggle();  //DrawerToggle 是一个用于显示和隐藏抽屉的内置组件。将其添加到布局中就足够了。您不需要为此编写任何代码。
        toggle.setAriaLabel("Menu toggle");  //为没有标题的组件设置 aria-label 对于可访问性非常重要。
        toggle.setTooltipText("Menu toggle");  //鼠标指针悬停在切换按钮上时，将显示工具提示文本。

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE,LumoUtility.Margin.NONE
            ,LumoUtility.Flex.GROW);  //这是本教程中第一次指定 CSS 样式。稍后将更详细地介绍样式。

        var header = new Header(toggle,viewTitle); //Header 是代表 <header> HTML 元素的 Vaadin 组件。
        header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL);

        addToNavbar(false,header); //这个设置可能看起来很奇怪。
        // 设置为 false 的布尔标志表示即使在移动设备上也将标题保留在顶部。将其设置为 true 会导致标题移动到移动设备上的屏幕底部。
    }

    private void addNavbarContent() {
        var appName = new Span("vaddin chat"); //Span 是代表 <span> HTML 元素的 Vaadin 组件。
        appName.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Height.XLARGE, LumoUtility.Padding.Horizontal.MEDIUM);
        addToDrawer(appName, new Scroller(createSideNav())); //SideNav 被包裹在 Scroller 组件内，以确保它能够滚动，以防它不适合屏幕。
    }

    private SideNav createSideNav() {
        SideNav nav = new SideNav(); //SideNav 是一个侧面导航菜单组件，支持平面和分层导航项。

        nav.addItem(new SideNavItem("Lobby", LobbyView.class, //侧面导航菜单将包含一个将用户导航到大厅视图的单个项目。
                VaadinIcon.BUILDING.create()));
        return nav;
    }

    //在 Vaadin Flow 应用程序中，页面标题可以是静态的，也可以是动态的。
    // 使用 @PageTitle 注释设置静态页面标题。动态页面标题是通过实现 Vaadin 提供的 HasDynamicTitle 接口来设置的。

    private String getCurrentPageTitle(){
        if(getContent()== null){
            return  "";
        }
        else if (getContent() instanceof HasDynamicTitle titleHolder){
            return titleHolder.getPageTitle();
        }else{
            var title = getContent().getClass().getAnnotation(PageTitle.class);
            return title == null ? "":title.value();
        }
    }
    //要使页面标题可见，您必须实现一个检索标题的方法。当布局内容发生变化时，您必须更新用户界面。
    //通过重写 afterNavigation() 方法在内容更改时更新用户界面：
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }
}
