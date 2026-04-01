package hotel.models;

public enum RoomType {
    SINGLE(100), DOUBLE(200), SUITE(500);
    private final int price;
    RoomType(int price) { this.price = price; }
    public int getPrice() { return price; }
}
