class Player{
  constructor(id, name){
    this.memory = 0;
    this.id = id;
    this.name = name;
  }  
}

class PlayerManagement{
  constructor(){
    this.players = new Map();
  }
  addPlayer(player){
    this.players.set(player.id, player);
    console.log("da them 1 player: " + player.name + ", id: " + player.id);
  }
  getPlayer(playerID){
    return this.players.get(playerID);
  }
  removePlayer(playerID){
    this.players.delete(playerID);
    console.log("nguoi choi " + playerID + " da thoat");
  }
  hasName(name){
    for(let pName of this.players.values()){
      if(name == pName.name){
        return true;
      }
    }
    return false;
  }
}

class BoardGame{

  constructor(){
    this.value = 1;
    this.player1GoFirst = false;
    this.isGameRunning = false;
  }
  getPlayer1Turn(){
    return this.player1GoFirst;
  }
  getPlayer2Turn(){
    return !this.player1GoFirst;
  }
  changesTurn(){
    this.player1GoFirst = !this.player1GoFirst;
  }

  newBoard(){
    this.cells = [
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0],
      [0,0,0,0,0,0,0,0,0,0,0,0]
    ];
    this.rowNum = this.cells.length;
    this.colNum = this.cells[0].length;
  }
  startGame(){
    this.newBoard();
    this.changesTurn();
    this.isGameRunning = true;
  }
  checkWin(x,y){
    let score = 1;
    // check duong cheo 1   * 
    // cheo tren        *
    //              *
    for(let i=1; i < 5; i++){
      if( x-i < 0 || y-i < 0){
        break;
      }
      if(this.cells[x-i][y-i] != this.cells[x][y]){
        break;
      }
      score++;
    }
    if(score >= 5){
      return true;
    }
      // cheo duoi
    for(let i=1; i<5; i++){
      if( x+i >= this.rowNum || y+i >= this.colNum){
        break;
      }
      if(this.cells[x+i][y+i] != this.cells[x][y]){
        break;
      }
      score++;
      if(score >= 5){
        return true;
      } 
    }
    score = 1;
    // check duong cheo 2       * 
    //              *
    //            *
      // treo tren
    for(let i=1; i < 5; i++){
      if( x-i <0 || y+i >= this.colNum){
        break;
      }
      if(this.cells[x-i][y+i] != this.cells[x][y]){
        break;
      }
      score++;
    }
    if(score >= 5){
      return true;
    }
      // cheo duoi
    for(let i=1; i<5; i++){
      if( x+i >= this.rowNum || y-i < 0){
        break;
      }
      if(this.cells[x+i][y-i] != this.cells[x][y]){
        break;
      }
      score++;
      if(score >= 5){
        return true;
      } 
    }
    score = 1;
    // check ngang
    // x ko doi
    // ngang trai
    for(let i=1; i < 5; i++){
      if(y-i < 0){
        break;
      }
      if(this.cells[x][y-i] != this.cells[x][y]){
        break;
      }
      score++;
    }
    if(score >= 5){
      return true;
    }
      // ngang phai
    for(let i=1; i<5; i++){
      if(y+i >= this.colNum){
        break;
      }
      if(this.cells[x][y+i] != this.cells[x][y]){
        break;
      }
      score++;
      if(score >= 5){
        return true;
      } 
    }
    score = 1;
    // chech doc
    // y ko doi
      // doc tren
    for(let i=1; i < 5; i++){
      if(x-i < 0){
        break;
      }
      if(this.cells[x-i][y] != this.cells[x][y]){
        break;
      }
      score++;
    }
    if(score >= 5){
      return true;
    }
      // doc duoi
    for(let i=1; i<5; i++){
      if(x+i >= this.rowNum){
        break;
      }
      if(this.cells[x+i][y] != this.cells[x][y]){
        break;
      }
      score++;
      if(score >= 5){
        return true;
      } 
    }

    return false;
  }
  go(x,y){
    this.cells[x][y] = this.value;
    this.value = -this.value;
  }
}

class Room{
  constructor(id, player, name){
    this.name = name;
    this.id = id;
    this.players = [player];
    this.boardGame = new BoardGame();
  }
  addPlayer(player){
    player.memory = this.id;
    this.players.push(player);
  }
  getPlayerNum(){
    return this.players.length;
  }
  removePlayer(playerID){
    for(let player of this.players){
      if(player.id == playerID){
        let index = this.players.indexOf(player);
        this.players.splice(index, 1);
        player.memory = 0;
        break;
      }
    }
  }
}

class RoomManagement{
  constructor(){
    this.rooms = new Map();
  }
  addRoom(room){
    this.rooms.set(room.id, room);
    console.log("da tao 1 room moi, id: " + room.id + ", ten : " + room.name);
  }
  getRoom(roomID){
    return this.rooms.get(roomID);
  }
  hasRoomID(roomID){
    return this.rooms.has(roomID);
  }
  removeRoom(roomID){
    this.rooms.delete(roomID);
    console.log("da xoa room " + roomID);
  }
  getNewRoomID(){
    let i = 0;
    for(i=1; i< 100; i++){
      if( !this.rooms.has(i) ){
          break;
      }
    }
    return i;
  }
  getRoomsInfo(){
    let roomsInfor = [];
    for(let room of this.rooms.values()){
      let roomInfor = {
        id: room.id, 
        name: room.name, 
        playerNum: room.players.length
      };
      roomsInfor.push(roomInfor);
    }
    return roomsInfor;
  }
}
//-------------------------------------
var p1 = new Player(123321,"Tran Van Do");
var p2 = new Player(1323132, "Ngu Thi Ba");
var p3 = new Player(12135, "Hai Ngai Qua");
var pm = new PlayerManagement();
pm.addPlayer(p1);
pm.addPlayer(p2);
pm.addPlayer(p3);

var rm = new RoomManagement();
var r1 = new Room(1,p1,"choi vui vui");
var r2 = new Room(2,p2,"vo dich");

r1.addPlayer(p3);

rm.addRoom(r1);
rm.addRoom(r2);
console.log(rm);

console.log(rm.getRoomsInfo());

r1.removePlayer(123321);
r1.addPlayer(p2);
console.log(r1.players[0]);

r2.removePlayer(p2.id);
console.log(r2.players.length);