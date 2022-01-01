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
  	// check duong cheo 1 	* 
  	// cheo tren			  *
  	//							*
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
  	// check duong cheo 2 	    * 
  	// 						  *
  	//						*
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
    player.memory = id;
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
    console.log("da xoa room: " + roomID);
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
//-------------------------------------------------------
var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
server.listen(process.env.PORT || 8787);

var pm = new PlayerManagement();
var rm = new RoomManagement();

//-------------------------------------
//tao du lieu mau
let p1 = new Player(123,"ABC");
let p2 = new Player(624,"Tran Van B");
let p3 = new Player(284,"Vo Dich");
let p4 = new Player(968,"Toi La Ai");
pm.addPlayer(p1);
pm.addPlayer(p2);
pm.addPlayer(p3);
pm.addPlayer(p4);

let r1 = new Room(1,p1,"choi vui vui");
r1.addPlayer(p3);
let r2 = new Room(2,p2,"ahihi");
r2.addPlayer(p4);
rm.addRoom(r1);
rm.addRoom(r2);

//-------------------------------------

io.sockets.on('connection', function(socket){
	console.log("an user connected, id: " + socket.id );
	
	socket.on("check user name", function(username){

		if(pm.hasName(username)){
			//nếu tên đã tồn tại
			socket.emit("response check user", "NO");
		}else{
			// tên cho phép
			let player = new Player(socket.id, username);
			pm.addPlayer(player);
			socket.emit("response check user", "OK");
		}
	});

	socket.on("get rooms infor", function(){
		socket.emit("reponse rooms infor", rm.getRoomsInfo());
	});

	socket.on("join room", function(roomID){
		let result = {};
		if(!rm.hasRoomID(roomID)){
			// phòng không tồn tại
			result.status = "NOPE";
		}else{
			let room = rm.getRoom(roomID);
			if(room.getPlayerNum() >= 2){
				//phòng đã đầy
				result.status = "FULL";
				
			}else{
				// ok vào được
				// ghi tên chủ phòng vào response
				result.status = "OK";
				result.competitorName = room.players[0].name;
				result.roomID = roomID;
				// lấy thông tin người chơi
				let player = pm.getPlayer(socket.id);
				
				// gửi tên khách cho chủ phòng
				io.sockets.connected[room.players[0].id].emit("someone join room", player.name);
				
				// thêm người chơi vào room
				room.addPlayer(player);
			}
		}
		socket.emit("reponse join room",result);
	});

	socket.on("create room", function(roomName){
		//tao id new room
		let nRoomID = rm.getNewRoomID();
		let player = pm.getPlayer(socket.id);
		let nRoom = new Room(nRoomID, player, roomName);
		rm.addRoom(nRoom);
		socket.emit("create room response", nRoomID);
	});

	socket.on("i am ready", function(roomID){
		let room = rm.getRoom(roomID);
		io.sockets.connected[room.players[0].id].emit("already");
	});

	socket.on("start game", function(roomID){
		let room = rm.getRoom(roomID);
		room.boardGame.startGame();
		io.sockets.connected[room.players[0].id].emit("response start game", room.boardGame.getPlayer1Turn());
		io.sockets.connected[room.players[1].id].emit("response start game", room.boardGame.getPlayer2Turn());
	});

	socket.on("go", function(data){
		let room = rm.getRoom(data.roomID);
		let nx = data.x;
		let ny = data.y;
		let otherPlayerID;
		if(socket.id == room.players[0].id){
			// nếu nó là người chơi một -> gửi dữ liệu cho máy 2
			otherPlayerID = room.players[1].id;
		}else{
			otherPlayerID = room.players[0].id;
		}

		// gửi nước đi cho đối thủ 
		io.sockets.connected[otherPlayerID].emit("no danh", {x: nx, y: ny});
		room.boardGame.go(nx, ny);

		if(room.boardGame.checkWin(nx, ny)){
			console.log("that su ik");
			socket.emit("you win");
			io.sockets.connected[otherPlayerID].emit("you lose");
			room.boardGame.isGameRunning = false;
		}else{
			// reset time
			socket.emit("change turn");
		}
	});

	socket.on("i quit", function(roomID){
		let room = rm.getRoom(roomID);
		// xóa player khỏi phòng
		room.removePlayer(socket.id);
		if(room.players.length == 0){
			// nếu số người trong phòng = 0 => xóa phòng
			rm.removeRoom(roomID);
		}else{
			// nếu còn người
			// gửi thông báo cho người đó
			io.sockets.connected[room.players[0].id].emit("someone left");
		}
	});

	socket.on("toi het gio", function(roomID){
		console.log("toi het gio");
		let room = rm.getRoom(roomID);
		if(room.boardGame.isGameRunning){
			room.boardGame.isGameRunning = false;
			socket.emit("you lose");
			if(socket.id == room.players[0].id){
				// nếu bạn là người chơi 0 => đối thủ là người chơi 1
				io.sockets.connected[room.players[1].id].emit("you win");
			}else{
				// ngược lại đối thủ là người chơi 0
				io.sockets.connected[room.players[0].id].emit("you win");
			}		
		}
	});

	socket.on("doi thu het gio", function(roomID){
		console.log("doi thu het gio");
		let room = rm.getRoom(roomID);
		if(room.boardGame.isGameRunning){
			room.boardGame.isGameRunning = false;
			socket.emit("you win");
			if(socket.id == room.players[0].id){
				// nếu bạn là người chơi 0 => đối thủ là người chơi 1
				io.sockets.connected[room.players[1].id].emit("you lose");
			}else{
				// ngược lại đối thủ là người chơi 0
				io.sockets.connected[room.players[0].id].emit("you lose");
			}			
		}
	});

	socket.on('disconnect', function(){
		let player = pm.getPlayer(socket.id);
		if(player.memory != 0){
			let room = rm.getRoom(player.memory);
			room.removePlayer(player.id);
			if(room.players.length == 0){
				rm.removeRoom(room.id);
			}else{
				if(room.boardGame.isGameRunning){
					io.sockets.connected[room.players[0].id].emit("you win");
				}
				io.sockets.connected[room.players[0].id].emit("someone left");
			}
		}
		pm.removePlayer(player.id);
	});
});

console.log("server is running...");