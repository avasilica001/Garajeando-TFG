<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['NombreComunidad'])){
                        $bbdd = new DBOperations();
                        $idComunidad = $bbdd->crearComunidad($_POST['NombreComunidad']);
                        if($idComunidad  == false || $idComunidad == 0){
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "Este nombre ya está en uso.";
                        }else{
				$datosComunidad = $bbdd->obtenerDatosComunidad($idComunidad);
                                if (count($datosComunidad) != 0){
                                        $respuesta['error'] = false;
                                        $respuesta['mensaje']['IdComunidad'] = $datosComunidad[0];
					$respuesta['mensaje']['Nombre'] = $datosComunidad[1];
					$respuesta['mensaje']['CodInvitacion'] = $datosComunidad[2];
					 
					$bbdd->anadirUsuarioAComunidad($_POST['IdUsuario'],$respuesta['mensaje']['IdComunidad'],1,'Administrador');
                                }else{
                                        $respuesta['error'] = true;
                                        $respuesta['mensaje'] = "El usuario ya pertenece a la comunidad a la que desea entrar.";
                                }
                        }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
