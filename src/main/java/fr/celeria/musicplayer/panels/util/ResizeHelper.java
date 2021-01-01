
package fr.celeria.musicplayer.panels.util;

import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeHelper {
    static boolean isScrollbar = false;

    public ResizeHelper() {
    }

    public static void addResizeListener(Stage stage) {
        addResizeListener(stage, 1.0D, 1.0D, 1.7976931348623157E308D, 1.7976931348623157E308D);
    }

    public static void addResizeListener(Stage stage, double minWidth, double minHeight, double maxWidth, double maxHeight) {
        ResizeHelper.ResizeListener resizeListener = new ResizeHelper.ResizeListener(stage);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
        resizeListener.setMinWidth(minWidth);
        resizeListener.setMinHeight(minHeight);
        resizeListener.setMaxWidth(maxWidth);
        resizeListener.setMaxHeight(maxHeight);
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
        Iterator var11 = children.iterator();

        while(var11.hasNext()) {
            Node child = (Node)var11.next();
            if (child instanceof ScrollBar) {
                isScrollbar = true;
            } else if (!(child instanceof ScrollBar)) {
                isScrollbar = false;
                addListenerDeeply(child, resizeListener);
            }
        }

    }

    private static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
        if (node instanceof Parent) {
            Parent parent = (Parent)node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            Iterator var4 = children.iterator();

            while(var4.hasNext()) {
                Node child = (Node)var4.next();
                if (child instanceof ScrollBar) {
                    isScrollbar = true;
                } else if (!(child instanceof ScrollBar)) {
                    isScrollbar = false;
                    addListenerDeeply(child, listener);
                }
            }
        }

    }

    static class ResizeListener implements EventHandler<MouseEvent> {
        private Stage stage;
        private Cursor cursorEvent;
        private boolean resizing;
        private double startX;
        private double startY;
        private double screenOffsetX;
        private double screenOffsetY;
        private double minWidth;
        private double maxWidth;
        private double minHeight;
        private double maxHeight;

        public ResizeListener(Stage stage) {
            this.cursorEvent = Cursor.DEFAULT;
            this.resizing = true;
            this.startX = 0.0D;
            this.startY = 0.0D;
            this.screenOffsetX = 0.0D;
            this.screenOffsetY = 0.0D;
            this.stage = stage;
        }

        public void setMinWidth(double minWidth) {
            this.minWidth = minWidth;
        }

        public void setMaxWidth(double maxWidth) {
            this.maxWidth = maxWidth;
        }

        public void setMinHeight(double minHeight) {
            this.minHeight = minHeight;
        }

        public void setMaxHeight(double maxHeight) {
            this.maxHeight = maxHeight;
        }

        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = this.stage.getScene();
            double mouseEventX = mouseEvent.getSceneX();
            double mouseEventY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();
            int border = 4;
            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                if (mouseEventX < (double)border && mouseEventY < (double)border) {
                    this.cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < (double)border && mouseEventY > sceneHeight - (double)border) {
                    this.cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - (double)border && mouseEventY < (double)border) {
                    this.cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - (double)border && mouseEventY > sceneHeight - (double)border) {
                    this.cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < (double)border) {
                    this.cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - (double)border) {
                    this.cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < (double)border) {
                    this.cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - (double)border) {
                    this.cursorEvent = Cursor.S_RESIZE;
                } else {
                    this.cursorEvent = Cursor.DEFAULT;
                }

                scene.setCursor(this.cursorEvent);
            } else if (!MouseEvent.MOUSE_EXITED.equals(mouseEventType) && !MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                    this.startX = this.stage.getWidth() - mouseEventX;
                    this.startY = this.stage.getHeight() - mouseEventY;
                } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) && !Cursor.DEFAULT.equals(this.cursorEvent)) {
                    this.resizing = true;
                    double minWidth;
                    if (!Cursor.W_RESIZE.equals(this.cursorEvent) && !Cursor.E_RESIZE.equals(this.cursorEvent)) {
                        minWidth = this.stage.getMinHeight() > (double)(border * 2) ? this.stage.getMinHeight() : (double)(border * 2);
                        if (!Cursor.NW_RESIZE.equals(this.cursorEvent) && !Cursor.N_RESIZE.equals(this.cursorEvent) && !Cursor.NE_RESIZE.equals(this.cursorEvent)) {
                            if (this.stage.getHeight() > minWidth || mouseEventY + this.startY - this.stage.getHeight() > 0.0D) {
                                this.setStageHeight(mouseEventY + this.startY);
                            }
                        } else if (this.stage.getHeight() > minWidth || mouseEventY < 0.0D) {
                            this.setStageHeight(this.stage.getY() - mouseEvent.getScreenY() + this.stage.getHeight());
                            this.stage.setY(mouseEvent.getScreenY());
                        }
                    }

                    if (!Cursor.N_RESIZE.equals(this.cursorEvent) && !Cursor.S_RESIZE.equals(this.cursorEvent)) {
                        minWidth = this.stage.getMinWidth() > (double)(border * 2) ? this.stage.getMinWidth() : (double)(border * 2);
                        if (!Cursor.NW_RESIZE.equals(this.cursorEvent) && !Cursor.W_RESIZE.equals(this.cursorEvent) && !Cursor.SW_RESIZE.equals(this.cursorEvent)) {
                            if (this.stage.getWidth() > minWidth || mouseEventX + this.startX - this.stage.getWidth() > 0.0D) {
                                this.setStageWidth(mouseEventX + this.startX);
                            }
                        } else if (this.stage.getWidth() > minWidth || mouseEventX < 0.0D) {
                            this.setStageWidth(this.stage.getX() - mouseEvent.getScreenX() + this.stage.getWidth());
                            this.stage.setX(mouseEvent.getScreenX());
                        }
                    }

                    this.resizing = false;
                }
            } else {
                scene.setCursor(Cursor.DEFAULT);
            }

            if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) && Cursor.DEFAULT.equals(this.cursorEvent)) {
                this.resizing = false;
                this.screenOffsetX = this.stage.getX() - mouseEvent.getScreenX();
                this.screenOffsetY = this.stage.getY() - mouseEvent.getScreenY();
            }

            if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) && Cursor.DEFAULT.equals(this.cursorEvent) && !this.resizing) {
                this.stage.setX(mouseEvent.getScreenX() + this.screenOffsetX);
                this.stage.setY(mouseEvent.getScreenY() + this.screenOffsetY);
            }

        }

        private void setStageWidth(double width) {
            width = Math.min(width, this.maxWidth);
            width = Math.max(width, this.minWidth);
            this.stage.setWidth(width);
        }

        private void setStageHeight(double height) {
            height = Math.min(height, this.maxHeight);
            height = Math.max(height, this.minHeight);
            this.stage.setHeight(height);
        }
    }
}
